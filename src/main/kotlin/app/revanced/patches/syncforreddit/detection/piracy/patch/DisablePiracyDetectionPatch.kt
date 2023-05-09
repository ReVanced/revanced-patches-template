package app.revanced.patches.syncforreddit.detection.piracy.patch

import app.revanced.extensions.error
import app.revanced.patcher.BytecodeContext
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patches.syncforreddit.detection.piracy.fingerprints.PiracyDetectionFingerprint

@Description("Disables detection of modified versions.")
@Version("0.0.1")
class DisablePiracyDetectionPatch : BytecodePatch(listOf(PiracyDetectionFingerprint)) {
    override fun execute(context: BytecodeContext) {
        PiracyDetectionFingerprint.result?.mutableMethod?.apply {
            addInstructions(
                0,
                """
                return-void
            """
            )
        } ?: PiracyDetectionFingerprint.error()
    }
}