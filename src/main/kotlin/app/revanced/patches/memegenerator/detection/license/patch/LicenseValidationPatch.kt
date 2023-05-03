package app.revanced.patches.memegenerator.detection.license.patch

import app.revanced.extensions.error
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.BytecodeContext
import app.revanced.patcher.extensions.replaceInstructions
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchException
import app.revanced.patches.memegenerator.detection.license.fingerprint.LicenseValidationFingerprint

@Description("Disables Firebase license validation.")
@Version("0.0.1")
class LicenseValidationPatch : BytecodePatch(
    listOf(LicenseValidationFingerprint)
) {
    override fun execute(context: BytecodeContext) {
        LicenseValidationFingerprint.result?.apply {
            mutableMethod.replaceInstructions(
                0,
                """
                    const/4 p0, 0x1
                    return  p0
                """
            )
        } ?: throw LicenseValidationFingerprint.error()
    }
}
