package app.revanced.patches.idaustria.detection.root.patch

import app.revanced.patcher.BytecodeContext
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.idaustria.detection.root.fingerprints.RootDetectionFingerprint
import app.revanced.patches.idaustria.detection.shared.annotations.DetectionCompatibility

@Patch
@Name("Remove root detection")
@Description("Removes the check for root permissions and unlocked bootloader.")
@DetectionCompatibility
@Version("0.0.1")
class RootDetectionPatch : BytecodePatch(
    listOf(RootDetectionFingerprint)
) {
    override suspend fun execute(context: BytecodeContext) {
        RootDetectionFingerprint.result!!.mutableMethod.addInstruction(0, "return-void")
    }
}
