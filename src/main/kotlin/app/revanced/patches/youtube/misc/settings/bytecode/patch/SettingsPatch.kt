package app.revanced.patches.youtube.misc.settings.bytecode.patch

import app.revanced.extensions.toErrorResult
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.addInstruction
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patches.shared.settings.preference.impl.Preference
import app.revanced.patches.shared.settings.util.AbstractPreferenceScreen
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
import app.revanced.patches.youtube.misc.settings.bytecode.fingerprints.LicenseActivityFingerprint
import app.revanced.patches.youtube.misc.settings.bytecode.fingerprints.ThemeSetterAppFingerprint
import app.revanced.patches.youtube.misc.settings.bytecode.fingerprints.ThemeSetterSystemFingerprint
import app.revanced.patches.youtube.misc.settings.resource.patch.SettingsResourcePatch
import org.jf.dexlib2.util.MethodUtil

@DependsOn(
    [
        IntegrationsPatch::class,
        SettingsResourcePatch::class,
    ]
)
@Name("settings")
@Description("Adds settings for ReVanced to YouTube.")
@Version("0.0.1")
class SettingsPatch : BytecodePatch(
    listOf(LicenseActivityFingerprint, ThemeSetterSystemFingerprint, ThemeSetterAppFingerprint)
) {
    override fun execute(context: BytecodeContext): PatchResult {
        fun buildInvokeInstructionsString(
            registers: String = "v0",
            classDescriptor: String = THEME_HELPER_DESCRIPTOR,
            methodName: String = SET_THEME_METHOD_NAME,
            parameters: String = "Ljava/lang/Object;"
        ) = "invoke-static {$registers}, $classDescriptor->$methodName($parameters)V"

        // apply the current theme of the settings page
        ThemeSetterSystemFingerprint.result!!.let { result ->
            val call = buildInvokeInstructionsString()
            result.mutableMethod.apply {
                addInstruction(
                    result.scanResult.patternScanResult!!.startIndex, call
                )
                addInstructions(
                    implementation!!.instructions.size - 1, call
                )
            }
        }

        // set the theme based on the preference of the app
        ThemeSetterAppFingerprint.result?.apply {
            fun buildInstructionsString(theme: Int) = """
                    const/4 v0, 0x$theme
                    ${buildInvokeInstructionsString(parameters = "I")}
                """

            val patternScanResult = scanResult.patternScanResult!!

            mutableMethod.apply {
                addInstructions(
                    patternScanResult.endIndex + 1, buildInstructionsString(1)
                )
                addInstructions(
                    patternScanResult.endIndex - 7, buildInstructionsString(0)
                )
                addInstructions(
                    patternScanResult.endIndex - 9, buildInstructionsString(1)
                )
                addInstructions(
                    implementation!!.instructions.size - 2, buildInstructionsString(0)
                )
            }
        } ?: return ThemeSetterAppFingerprint.toErrorResult()

        // set the theme based on the preference of the device
        LicenseActivityFingerprint.result!!.apply licenseActivity@{
            mutableMethod.apply {
                fun buildSettingsActivityInvokeString(
                    registers: String = "p0",
                    classDescriptor: String = SETTINGS_ACTIVITY_DESCRIPTOR,
                    methodName: String = "initializeSettings",
                    parameters: String = this@licenseActivity.mutableClass.type
                ) = buildInvokeInstructionsString(registers, classDescriptor, methodName, parameters)

                // initialize the settings
                addInstructions(
                    1, """
                        ${buildSettingsActivityInvokeString()}
                        return-void
                    """
                )

                // set the current theme
                addInstruction(0, buildSettingsActivityInvokeString(methodName = "setTheme"))
            }

            // remove method overrides
            mutableClass.apply {
                methods.removeIf { it.name != "onCreate" && !MethodUtil.isConstructor(it) }
            }
        }

        return PatchResultSuccess()
    }

    internal companion object {
        private const val INTEGRATIONS_PACKAGE = "app/revanced/integrations"

        private const val SETTINGS_ACTIVITY_DESCRIPTOR = "L$INTEGRATIONS_PACKAGE/settingsmenu/ReVancedSettingActivity;"

        private const val THEME_HELPER_DESCRIPTOR = "L$INTEGRATIONS_PACKAGE/utils/ThemeHelper;"
        private const val SET_THEME_METHOD_NAME = "setTheme"

        fun addString(identifier: String, value: String, formatted: Boolean = true) =
            SettingsResourcePatch.addString(identifier, value, formatted)

        fun addPreferenceScreen(preferenceScreen: app.revanced.patches.shared.settings.preference.impl.PreferenceScreen) =
            SettingsResourcePatch.addPreferenceScreen(preferenceScreen)

        fun addPreference(preference: Preference) = SettingsResourcePatch.addPreference(preference)

        fun renameIntentsTargetPackage(newPackage: String) {
            SettingsResourcePatch.overrideIntentsTargetPackage = newPackage
        }
    }

    /**
     * Preference screens patches should add their settings to.
     */
    internal object PreferenceScreen : AbstractPreferenceScreen() {
        val ADS = Screen("ads", "Ads", "Ad related settings")
        val INTERACTIONS = Screen("interactions", "Interaction", "Settings related to interactions")
        val LAYOUT = Screen("layout", "Layout", "Settings related to the layout")
        val MISC = Screen("misc", "Misc", "Miscellaneous patches")

        override fun commit(screen: app.revanced.patches.shared.settings.preference.impl.PreferenceScreen) {
            addPreferenceScreen(screen)
        }
    }

    override fun close() = PreferenceScreen.close()
}
