package app.revanced.patches.finanzonline.detection.bootloader.patch

import app.revanced.extensions.error
import app.revanced.patcher.BytecodeContext
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.extensions.addInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchException
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.finanzonline.detection.bootloader.fingerprints.BootStateFingerprint
import app.revanced.patches.finanzonline.detection.bootloader.fingerprints.BootloaderDetectionFingerprint
import app.revanced.patches.finanzonline.detection.shared.annotations.DetectionCompatibility


@Patch
@Name("remove-bootloader-detection")
@Description("Removes the check for an unlocked bootloader.")
@DetectionCompatibility
@Version("0.0.1")
class BootloaderDetectionPatch : BytecodePatch(
    listOf(BootloaderDetectionFingerprint, BootStateFingerprint)
) {
    override fun execute(context: BytecodeContext) {
        arrayOf(BootloaderDetectionFingerprint, BootStateFingerprint).forEach { fingerprint ->
            fingerprint.result?.mutableMethod?.addInstruction(
                0,
                """
                        const/4 v0, 0x1
                        return v0
                """
            ) ?: return fingerprint.error()
        }
    }
}
