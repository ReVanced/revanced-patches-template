package app.revanced.patches.reddit.customclients.syncforreddit.detection.piracy.patch

import app.revanced.extensions.error
import app.revanced.patcher.BytecodeContext
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patches.syncforreddit.detection.piracy.fingerprints.PiracyDetectionFingerprint

@Description("Disables detection of modified versions.")
@Version("0.0.1")
class DisablePiracyDetectionPatch : BytecodePatch(listOf(PiracyDetectionFingerprint)) {
    override suspend fun execute(context: BytecodeContext) {
        PiracyDetectionFingerprint.result?.mutableMethod?.apply {
            addInstruction(
                0,
                """
                    return-void
                """
            )
        } ?: PiracyDetectionFingerprint.error()
    }
}