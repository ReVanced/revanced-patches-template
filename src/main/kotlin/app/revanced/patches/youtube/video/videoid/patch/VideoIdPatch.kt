package app.revanced.patches.youtube.video.videoid.patch

import app.revanced.extensions.error
import app.revanced.patcher.BytecodeContext
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
import app.revanced.patches.youtube.video.videoid.annotation.VideoIdCompatibility
import app.revanced.patches.youtube.video.videoid.fingerprint.VideoIdFingerprint
import app.revanced.patches.youtube.video.videoid.fingerprint.VideoIdFingerprintBackgroundPlay
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction

@Name("Video id hook")
@Description("Hooks to detect when the video id changes")
@VideoIdCompatibility
@Version("0.0.1")
@DependsOn([IntegrationsPatch::class])
class VideoIdPatch : BytecodePatch(
    listOf(VideoIdFingerprint, VideoIdFingerprintBackgroundPlay)
) {
    override suspend fun execute(context: BytecodeContext) {
        VideoIdFingerprint.result?.let { result ->
            val videoIdRegisterInstructionIndex = result.scanResult.patternScanResult!!.endIndex

            result.mutableMethod.also {
                insertMethod = it
            }.apply {
                videoIdRegister = getInstruction<OneRegisterInstruction>(videoIdRegisterInstructionIndex).registerA
                insertIndex = videoIdRegisterInstructionIndex + 1
            }
        } ?: VideoIdFingerprint.error()

        VideoIdFingerprintBackgroundPlay.result?.let { result ->
            val endIndex = result.scanResult.patternScanResult!!.endIndex

            result.mutableMethod.also {
                backgroundPlaybackMethod = it
            }.apply {
                backgroundPlaybackVideoIdRegister = getInstruction<OneRegisterInstruction>(endIndex + 1).registerA
                backgroundPlaybackInsertIndex = endIndex + 2
            }
        } ?: VideoIdFingerprintBackgroundPlay.error()

    }

    companion object {
        private var videoIdRegister = 0
        private var insertIndex = 0
        private lateinit var insertMethod: MutableMethod

        private var backgroundPlaybackVideoIdRegister = 0
        private var backgroundPlaybackInsertIndex = 0
        private lateinit var backgroundPlaybackMethod: MutableMethod

        /**
         * Adds an invoke-static instruction, called with the new id when the video changes.
         *
         * Supports all videos (regular videos, Shorts and Stories).
         *
         * _Does not function if playing in the background with no video visible_.
         *
         * Be aware, this can be called multiple times for the same video id.
         *
         * @param methodDescriptor which method to call. Params have to be `Ljava/lang/String;`
         */
        fun injectCall(
            methodDescriptor: String
        ) = insertMethod.addInstruction(
            // Keep injection calls in the order they're added:
            // Increment index. So if additional injection calls are added, those calls run after this injection call.
            insertIndex++,
            "invoke-static {v$videoIdRegister}, $methodDescriptor"
        )

        /**
         * Alternate hook that supports only regular videos, but hook supports changing to new video
         * during background play when no video is visible.
         *
         * _Does not support Shorts or Stories_.
         *
         * Be aware, the hook can be called multiple times for the same video id.
         *
         * @param methodDescriptor which method to call. Params have to be `Ljava/lang/String;`
         */
        fun injectCallBackgroundPlay(
            methodDescriptor: String
        ) = backgroundPlaybackMethod.addInstruction(
            backgroundPlaybackInsertIndex++, // move-result-object offset
                "invoke-static {v$backgroundPlaybackVideoIdRegister}, $methodDescriptor"
            )
    }
}

