package app.revanced.patches.idaustria.detection.root

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patches.idaustria.detection.root.fingerprints.AttestationSupportedCheckFingerprint
import app.revanced.patches.idaustria.detection.root.fingerprints.BootloaderCheckFingerprint
import app.revanced.patches.idaustria.detection.root.fingerprints.RootCheckFingeprint

@Patch(
    name = "Remove root detection",
    description = "Removes the check for root permissions and unlocked bootloader.",
    compatiblePackages = [CompatiblePackage("at.gv.oe.app", ["3.0.2"])]
)
@Suppress("unused")
object RootDetectionPatch : BytecodePatch(
    setOf(AttestationSupportedCheckFingerprint, BootloaderCheckFingerprint, RootCheckFingeprint)
) {
    override fun execute(context: BytecodeContext) {
        AttestationSupportedCheckFingerprint.result!!.mutableMethod.addInstruction(0, "return-void")
        BootloaderCheckFingerprint.result!!.mutableMethod.addInstructions(
            0, """
            const v0, 0x1
            return v0
        """
        )
        RootCheckFingeprint.result!!.mutableMethod.addInstruction(0, "return-void")
    }
}
