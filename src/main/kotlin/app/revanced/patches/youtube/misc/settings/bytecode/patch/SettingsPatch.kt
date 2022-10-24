package app.revanced.patches.youtube.misc.settings.bytecode.patch

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
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
import app.revanced.patches.youtube.misc.mapping.patch.ResourceMappingResourcePatch
import app.revanced.patches.youtube.misc.settings.annotations.SettingsCompatibility
import app.revanced.patches.youtube.misc.settings.bytecode.fingerprints.LicenseActivityFingerprint
import app.revanced.patches.youtube.misc.settings.bytecode.fingerprints.ReVancedSettingsActivityFingerprint
import app.revanced.patches.youtube.misc.settings.bytecode.fingerprints.ThemeSetterSystemFingerprint
import app.revanced.patches.youtube.misc.settings.bytecode.fingerprints.ThemeSetterAppFingerprint
import app.revanced.patches.youtube.misc.settings.framework.components.BasePreference
import app.revanced.patches.youtube.misc.settings.framework.components.impl.ArrayResource
import app.revanced.patches.youtube.misc.settings.framework.components.impl.Preference
import app.revanced.patches.youtube.misc.settings.framework.components.impl.PreferenceScreen
import app.revanced.patches.youtube.misc.settings.framework.components.impl.StringResource
import app.revanced.patches.youtube.misc.settings.resource.patch.SettingsResourcePatch
import org.jf.dexlib2.util.MethodUtil
import java.io.Closeable

@Patch
@DependsOn(
    [
        IntegrationsPatch::class,
        SettingsResourcePatch::class,
    ]
)
@Name("settings")
@Description("Adds settings for ReVanced to YouTube.")
@SettingsCompatibility
@Version("0.0.1")
class SettingsPatch : BytecodePatch(
    listOf(LicenseActivityFingerprint, ReVancedSettingsActivityFingerprint, ThemeSetterSystemFingerprint, ThemeSetterAppFingerprint)
) {
    override fun execute(context: BytecodeContext): PatchResult {
        val licenseActivityResult = LicenseActivityFingerprint.result!!
        val licenseActivityMutableClass = licenseActivityResult.mutableClass
        val settingsResult = ReVancedSettingsActivityFingerprint.result!!

        // add instructions to set the theme of the settings activity, based on Android system tint
        with(ThemeSetterSystemFingerprint.result!!) {
            with(mutableMethod) {
                val setSystemThemeInstruction =
                    "invoke-static {v0}, Lapp/revanced/integrations/utils/ThemeHelper;->setTheme(Ljava/lang/Object;)V"

                addInstruction(
                    scanResult.patternScanResult!!.startIndex,
                    setSystemThemeInstruction
                )

                addInstruction(
                    mutableMethod.implementation!!.instructions.size - 1,
                    setSystemThemeInstruction
                )
            }
        }

        // add instructions to set the theme of the settings activity, based on app tint
        with(ThemeSetterAppFingerprint.result!!) {
            with(mutableMethod) {
                fun setAppThemeInstructions(value: Int) = """
                    const/4 v0, 0x$value
                    invoke-static {v0}, Lapp/revanced/integrations/utils/ThemeHelper;->setTheme(I)V
                """

                addInstructions(
                    scanResult.patternScanResult!!.endIndex + 1,
                    setAppThemeInstructions(1)
                )

                addInstructions(
                    mutableMethod.implementation!!.instructions.size - 2,
                    setAppThemeInstructions(0)
                )
            }
        }

        with(licenseActivityResult) {
            with(mutableMethod) {
                fun licenseActivityInvokeInstruction(classname: String, returnVoid: Boolean) = """
                    invoke-static {p0}, ${settingsResult.mutableClass.type}->${classname}(${licenseActivityMutableClass.type})V
                    ${if (returnVoid) "return-void" else ""}
                """

                // add the setTheme call to the onCreate method to not affect the offsets
                addInstruction(
                    1,
                    licenseActivityInvokeInstruction(name,true)
                )

                // add the initializeSettings call to the onCreate method
                addInstruction(
                    0,
                    licenseActivityInvokeInstruction("setTheme",false)
                )

                // get rid of, now, useless overridden methods
                with(mutableClass) {
                    methods.removeIf { it.name != "onCreate" && !MethodUtil.isConstructor(it) }
                }
            }
        }

        return PatchResultSuccess()
    }

    internal companion object {
        // TODO: hide this somehow
        var appearanceStringId: Long = ResourceMappingResourcePatch.resourceMappings.find {
            it.type == "string" && it.name == "app_theme_appearance_dark"
        }!!.id

        fun addString(identifier: String, value: String, formatted: Boolean = true) =
            SettingsResourcePatch.addString(identifier, value, formatted)

        fun addPreferenceScreen(preferenceScreen: app.revanced.patches.youtube.misc.settings.framework.components.impl.PreferenceScreen) =
            SettingsResourcePatch.addPreferenceScreen(preferenceScreen)

        fun addPreference(preference: Preference) =
            SettingsResourcePatch.addPreference(preference)

        fun renameIntentsTargetPackage(newPackage: String) {
            SettingsResourcePatch.overrideIntentsTargetPackage = newPackage
        }
    }

    /**
     * Preference screens patches should add their settings to.
     */
    internal enum class PreferenceScreen(
        private val key: String,
        private val title: String,
        private val summary: String? = null,
        private val preferences: MutableList<BasePreference> = mutableListOf()
    ) : Closeable {
        ADS("ads", "Ads", "Ad related settings"),
        INTERACTIONS("interactions", "Interaction", "Settings related to interactions"),
        LAYOUT("layout", "Layout", "Settings related to the layout"),
        MISC("misc", "Miscellaneous", "Miscellaneous patches");

        override fun close() {
            if (preferences.size == 0) return

            addPreferenceScreen(
                PreferenceScreen(
                    key,
                    StringResource("${key}_title", title),
                    preferences,
                    summary?.let { summary ->
                        StringResource("${key}_summary", summary)
                    }
                )
            )
        }

        /**
         * Add preferences to the preference screen.
         */
        fun addPreferences(vararg preferences: BasePreference) = this.preferences.addAll(preferences)
    }

    override fun close() = PreferenceScreen.values().forEach(PreferenceScreen::close)

}
