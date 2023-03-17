package app.revanced.patches.youtube.misc.video.videoid.patch

import app.revanced.extensions.toErrorResult
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.extensions.instruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
import app.revanced.patches.youtube.misc.video.videoid.annotation.VideoIdCompatibility
import app.revanced.patches.youtube.misc.video.videoid.fingerprint.VideoIdFingerprint
import app.revanced.patches.youtube.misc.video.videoid.fingerprint.VideoIdFingerprintBackgroundPlay
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction

@Name("video-id-hook")
@Description("Hooks to detect when the video id changes")
@VideoIdCompatibility
@Version("0.0.1")
@DependsOn([IntegrationsPatch::class])
class VideoIdPatch : BytecodePatch(
    listOf(VideoIdFingerprint, VideoIdFingerprintBackgroundPlay)
) {
    override fun execute(context: BytecodeContext): PatchResult {
        VideoIdFingerprint.result?.let {
            val videoIdRegisterInstructionIndex = it.scanResult.patternScanResult!!.endIndex

            with(it.mutableMethod) {
                insertMethod = this
                videoIdRegister = (instruction(videoIdRegisterInstructionIndex) as OneRegisterInstruction).registerA
                insertIndex = videoIdRegisterInstructionIndex + 1
            }
        } ?: return VideoIdFingerprint.toErrorResult()

        VideoIdFingerprintBackgroundPlay.result?.let {
            val endIndex = it.scanResult.patternScanResult!!.endIndex

            with(it.mutableMethod) {
                backgroundPlayInsertMethod = this
                backgroundPlayVideoIdRegister = (instruction(endIndex + 1) as OneRegisterInstruction).registerA
                backgroundPlayInsertIndex = endIndex + 2
            }
        } ?: return VideoIdFingerprintBackgroundPlay.toErrorResult()

        return PatchResultSuccess()
    }

    companion object {
        private var videoIdRegister = 0
        private var insertIndex = 0

        private lateinit var insertMethod: MutableMethod

        /**
         * Adds an invoke-static instruction, called with the new id when the video changes
         *
         * Supports all videos (regular videos, Shorts and Stories)
         *
         * _Does not function if playing in the background with no video visible_
         *
         * Be aware, this can be called multiple times for the same video id.
         *
         * @param methodDescriptor which method to call. Params have to be `Ljava/lang/String;`
         */
        fun injectCall(
            methodDescriptor: String
        ) = insertMethod.addInstructions(
                // TODO: The order has been proven to not be required, so remove the logic for keeping order.
                // Keep injection calls in the order they're added:
                // Increment index. So if additional injection calls are added, those calls run after this injection call.
                insertIndex++,
                "invoke-static {v$videoIdRegister}, $methodDescriptor"
            )

        private var backgroundPlayVideoIdRegister = 0
        private var backgroundPlayInsertIndex = 0
        private lateinit var backgroundPlayInsertMethod: MutableMethod

        /**
         * Alternate hook that supports only regular videos, but hook supports changing to new video
         * during background play when no video is visible
         *
         * _Does not support Shorts or Stories_
         *
         * Be aware, the hook can be called multiple times for the same video id.
         *
         * @param methodDescriptor which method to call. Params have to be `Ljava/lang/String;`
         */
        fun injectCallBackgroundPlay(
            methodDescriptor: String
        ) = backgroundPlayInsertMethod.addInstructions(
                backgroundPlayInsertIndex++, // move-result-object offset
                "invoke-static {v$backgroundPlayVideoIdRegister}, $methodDescriptor"
            )
    }
}

