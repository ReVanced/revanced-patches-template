package app.revanced.patches.youtube.misc.video.videoid.patch

import app.revanced.extensions.toErrorResult
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
import app.revanced.patches.youtube.misc.video.videoid.annotation.VideoIdCompatibility
import app.revanced.patches.youtube.misc.video.videoid.fingerprint.VideoIdFingerprint
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction

@Name("video-id-hook")
@Description("Hooks to detect when the video id changes")
@VideoIdCompatibility
@Version("0.0.1")
@DependsOn([IntegrationsPatch::class])
class VideoIdPatch : BytecodePatch(
    listOf(
        VideoIdFingerprint
    )
) {
    companion object {
        private var videoIdRegister = 0
        private var insertIndex = 0

        private lateinit var insertMethod: MutableMethod

        /**
         * Adds an invoke-static instruction, called with the new id when the video changes
         * @param methodDescriptor which method to call. Params have to be `Ljava/lang/String;`
         */
        fun injectCall(
            methodDescriptor: String
        ) {
            insertMethod.addInstructions(
                insertIndex++, // keep injection callbacks in the order they were added
                "invoke-static {v$videoIdRegister}, $methodDescriptor"
            )
        }
    }

    override fun execute(context: BytecodeContext): PatchResult {
        VideoIdFingerprint.result?.let {
            var videoIdRegisterInstructionIndex = it.scanResult.patternScanResult!!.endIndex;

            with (it.mutableMethod) {
                insertMethod = this
                videoIdRegister = (implementation!!.instructions[videoIdRegisterInstructionIndex] as OneRegisterInstruction).registerA
                insertIndex = videoIdRegisterInstructionIndex + 1;
            }
        } ?: return VideoIdFingerprint.toErrorResult()

        return PatchResultSuccess()
    }
}

