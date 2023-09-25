package app.revanced.patches.youtube.video.videoid

import app.revanced.extensions.exception
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import app.revanced.patches.youtube.misc.fix.playback.SpoofSignaturePatch
import app.revanced.patches.youtube.misc.integrations.IntegrationsPatch
import app.revanced.patches.youtube.misc.playertype.PlayerTypeHookPatch
import app.revanced.patches.youtube.video.videoid.fingerprint.PlayerParameterBuilderFingerprint
import app.revanced.patches.youtube.video.videoid.fingerprint.VideoIdFingerprint
import app.revanced.patches.youtube.video.videoid.fingerprint.VideoIdFingerprintBackgroundPlay
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction

@Patch(
    description = "Hooks to detect when the video id changes",
    dependencies = [IntegrationsPatch::class],
)
object VideoIdPatch : BytecodePatch(
    setOf(
        PlayerParameterBuilderFingerprint,
        VideoIdFingerprint,
        VideoIdFingerprintBackgroundPlay
    )
) {
    private const val playerResponseVideoIdParameter = 1
    private const val playerResponseProtoBufferParameter = 3
    /**
     * Insert index when adding a video id hook.
     */
    private var playerResponseVideoIdInsertIndex = 0
    /**
     * Insert index when adding a proto buffer override.
     * Must be after all video id hooks in the same method.
     */
    private var playerResponseProtoBufferInsertIndex = 0
    private lateinit var playerResponseMethod: MutableMethod

    private var videoIdRegister = 0
    private var insertIndex = 0
    private lateinit var insertMethod: MutableMethod

    private var backgroundPlaybackVideoIdRegister = 0
    private var backgroundPlaybackInsertIndex = 0
    private lateinit var backgroundPlaybackMethod: MutableMethod

    override fun execute(context: BytecodeContext) {

        // Hook player parameter.
        PlayerParameterBuilderFingerprint.result?.let {
            playerResponseMethod = it.mutableMethod
        } ?: throw PlayerParameterBuilderFingerprint.exception

        /**
         * Supplies the method and register index of the video id register.
         *
         * @param consumer Consumer that receives the method, insert index and video id register index.
         */
        fun MethodFingerprint.setFields(consumer: (MutableMethod, Int, Int) -> Unit) = result?.let { result ->
            val videoIdRegisterIndex = result.scanResult.patternScanResult!!.endIndex

            result.mutableMethod.let {
                val videoIdRegister = it.getInstruction<OneRegisterInstruction>(videoIdRegisterIndex).registerA
                val insertIndex = videoIdRegisterIndex + 1
                consumer(it, insertIndex, videoIdRegister)

            }
        } ?: throw VideoIdFingerprint.exception

        VideoIdFingerprint.setFields { method, insertIndex, videoIdRegister ->
            insertMethod = method
            VideoIdPatch.insertIndex = insertIndex
            VideoIdPatch.videoIdRegister = videoIdRegister
        }

        VideoIdFingerprintBackgroundPlay.setFields { method, insertIndex, videoIdRegister ->
            backgroundPlaybackMethod = method
            backgroundPlaybackInsertIndex = insertIndex
            backgroundPlaybackVideoIdRegister = videoIdRegister
        }
    }

    /**
     * Modify the player parameter proto buffer value.
     * Used exclusively by [SpoofSignaturePatch].
     */
    fun injectProtoBufferHook(methodDescriptor: String) {
        playerResponseMethod.addInstructions(
            playerResponseProtoBufferInsertIndex,
            """
               invoke-static {p$playerResponseProtoBufferParameter}, $methodDescriptor
               move-result-object p$playerResponseProtoBufferParameter
            """
        )
        playerResponseProtoBufferInsertIndex += 2
    }

    /**
     * Adds an invoke-static instruction, called with the new id when the video changes.
     *
     * Called as soon as the player response is parsed, and called before many other hooks are
     * updated such as [PlayerTypeHookPatch].
     *
     * Supports all videos and functions in all situations.
     *
     * Be aware, this can be called multiple times for the same video id.
     *
     * @param methodDescriptor which method to call. Params have to be `Ljava/lang/String;`
     */
    fun injectCall(methodDescriptor: String) {
        playerResponseMethod.addInstruction(
            // Keep injection calls in the order they're added,
            // and all video id hooks run before proto buffer hooks.
            playerResponseVideoIdInsertIndex++,
            "invoke-static {p$playerResponseVideoIdParameter}, $methodDescriptor"
        )
        playerResponseProtoBufferInsertIndex++
    }

    /**
     * Adds an invoke-static instruction, called with the new id when the video changes.
     *
     * Supports all videos (regular videos and Shorts).
     *
     * _Does not function if playing in the background with no video visible_.
     *
     * Be aware, this can be called multiple times for the same video id.
     *
     * @param methodDescriptor which method to call. Params have to be `Ljava/lang/String;`
     */
    fun legacyInjectCall(
        methodDescriptor: String
    ) = insertMethod.addInstruction(
        insertIndex++,
        "invoke-static {v$videoIdRegister}, $methodDescriptor"
    )

    /**
     * Alternate hook that supports only regular videos, but hook supports changing to new video
     * during background play when no video is visible.
     *
     * _Does not support Shorts_.
     *
     * Be aware, the hook can be called multiple times for the same video id.
     *
     * @param methodDescriptor which method to call. Params have to be `Ljava/lang/String;`
     */
    fun legacyInjectCallBackgroundPlay(
        methodDescriptor: String
    ) = backgroundPlaybackMethod.addInstruction(
        backgroundPlaybackInsertIndex++, // move-result-object offset
        "invoke-static {v$backgroundPlaybackVideoIdRegister}, $methodDescriptor"
    )
}

