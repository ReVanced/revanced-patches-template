package app.revanced.patches.shared.misc.pairip.license

import app.revanced.patcher.extensions.addInstruction
import app.revanced.patcher.patch.booleanOption
import app.revanced.patcher.patch.bytecodePatch
import app.revanced.util.returnEarly
import java.util.logging.Logger

@Suppress("unused")
val disablePairIPLicenseCheckPatch = bytecodePatch(
    name = "Disable PairIP license check",
    description = "Disable PairIP license and VM checks.",
    use = false,
) {
    val enableVmLogging by booleanOption(
        name = "Enable VM logging",
        description = "Enables detailed native library and VM logging for debugging PairIP.",
        default = false,
    )

    apply {
        val logger = Logger.getLogger(this::class.java.name)
        fun logMissing(tag: String) =
            logger.warning("Could not find PairIP method '$tag'.")

        verifyIntegrityMethod?.returnEarly()
            ?: logMissing("verifyIntegrityMethod")

        // Set first parameter (responseCode) to 0 (success status).
        processLicenseResponseMethod?.addInstruction(0, "const/4 p1, 0x0")
            ?: logMissing("processLicenseResponseMethod")

        // Short-circuit the license response validation.
        validateLicenseResponseMethod?.returnEarly()
            ?: logMissing("validateLicenseResponseMethod")

        // Always report a trusted local installer (passes the installer origin check).
        checkLocalInstallerMethod?.returnEarly(true)
            ?: logMissing("checkLocalInstallerMethod")

        // Disable repeated background checks.
        licenseClientClinit?.addInstruction(
            0,
            """
                const/4 v0, 0x0
                sput-boolean v0, Lcom/pairip/licensecheck/LicenseClient;->repeatedCheckEnabled:Z
            """
        ) ?: logMissing("licenseClientClinit")

        launchVMMethod?.returnEarly()
            ?: logMissing("launchVMMethod")

        isDebuggingEnabledMethod?.returnEarly(enableVmLogging == true)
            ?: logMissing("isDebuggingEnabledMethod")
    }
}
