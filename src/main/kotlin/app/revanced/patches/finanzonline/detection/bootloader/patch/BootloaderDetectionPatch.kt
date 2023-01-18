package app.revanced.patches.finanzonline.detection.bootloader.patch

import app.revanced.patcher.annotation.Compatibility
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Package
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.finanzonline.detection.bootloader.fingerprints.BootStateFingerprint
import app.revanced.patches.finanzonline.detection.bootloader.fingerprints.BootloaderDetectionFingerprint


@Patch
@Name("remove-bootloader-detection")
@Description("Removes the check for an unlocked bootloader")
@Compatibility([Package("at.gv.bmf.bmf2go", arrayOf("2.2.0"))])
@Version("0.0.1")
class BootloaderDetectionPatch : BytecodePatch(
    listOf(BootloaderDetectionFingerprint, BootStateFingerprint)
) {
    override fun execute(context: BytecodeContext): PatchResult {
        val returnTrue = """
            const/4 v0, 0x1
            return v0
        """

        BootloaderDetectionFingerprint.result!!.mutableMethod.addInstructions(0, returnTrue)
        BootStateFingerprint.result!!.mutableMethod.addInstructions(0, returnTrue)

        return PatchResultSuccess()
    }
}
