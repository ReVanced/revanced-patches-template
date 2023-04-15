package app.revanced.patches.memegenerator.detection.license.patch

import app.revanced.extensions.toErrorResult
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.replaceInstructions
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patches.memegenerator.detection.license.annotations.DisableLicenseValidationCompatibility
import app.revanced.patches.memegenerator.detection.license.fingerprint.LicenseValidationFingerprint

@Description("Disable FireBase license validation in meme generator.")
@DisableLicenseValidationCompatibility
@Version("0.0.1")
class LicenseValidationPatch : BytecodePatch(
    listOf(LicenseValidationFingerprint)
) {
    override fun execute(context: BytecodeContext): PatchResult {
        LicenseValidationFingerprint.result?.apply {
            mutableMethod.replaceInstructions(0, """
                const/4 p0, 0x1
                return  p0
            """.trimIndent())
        } ?: throw LicenseValidationFingerprint.toErrorResult()

        return PatchResultSuccess()
    }
}