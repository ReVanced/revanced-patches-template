package app.revanced.patches.youtube.layout.theme

import app.revanced.extensions.exception
import app.revanced.extensions.indexOfFirstWideLiteralInstructionValue
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patcher.patch.options.PatchOption.PatchExtensions.stringPatchOption
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.youtube.layout.seekbar.SeekbarColorBytecodePatch
import app.revanced.patches.youtube.layout.theme.fingerprints.UseGradientLoadingScreenFingerprint
import app.revanced.patches.youtube.misc.integrations.IntegrationsPatch
import app.revanced.patches.youtube.misc.strings.StringsPatch
import app.revanced.patches.youtube.misc.settings.SettingsPatch
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction

@Patch(
    name = "Theme",
    description = "Applies a custom theme.",
    dependencies = [
        LithoColorHookPatch::class,
        SeekbarColorBytecodePatch::class,
        ThemeResourcePatch::class,
        IntegrationsPatch::class,
        SettingsPatch::class
    ],
    compatiblePackages = [
        CompatiblePackage(
            "com.google.android.youtube",
            [
                "18.37.36",
                "18.38.44"
            ]
        )
    ]
)
@Suppress("unused")
object ThemeBytecodePatch : BytecodePatch(
    setOf(UseGradientLoadingScreenFingerprint)
) {
    private const val INTEGRATIONS_CLASS_DESCRIPTOR =
        "Lapp/revanced/integrations/patches/theme/ThemePatch;"

    internal const val GRADIENT_LOADING_SCREEN_AB_CONSTANT = 45412406L

    private const val AMOLED_BLACK_COLOR = "@android:color/black"
    private const val WHITE_COLOR = "@android:color/white"

    internal val darkThemeBackgroundColor by stringPatchOption(
        key = "darkThemeBackgroundColor",
        default = AMOLED_BLACK_COLOR,
        values = mapOf(
            "Amoled black" to AMOLED_BLACK_COLOR,
            "Material You" to "@android:color/system_neutral1_900",
            "Catppuccin (Mocha)" to "#FF181825",
            "Dark pink" to "#FF290025",
            "Dark blue" to "#FF001029",
            "Dark green" to "#FF002905",
            "Dark yellow" to "#FF282900",
            "Dark orange" to "#FF291800",
            "Dark red" to "#FF290000"
        ),
        title = "Dark theme background color",
        description = "Can be a hex color (#AARRGGBB) or a color resource reference.",
    )

    internal val lightThemeBackgroundColor by stringPatchOption(
        key = "lightThemeBackgroundColor",
        default = WHITE_COLOR,
        values = mapOf(
            "White" to WHITE_COLOR,
            "Material You" to "@android:color/system_neutral1_50",
            "Catppuccin (Latte)" to "#FFE6E9EF",
            "Light pink" to "#FFFCCFF3",
            "Light blue" to "#FFD1E0FF",
            "Light green" to "#FFCCFFCC",
            "Light yellow" to "#FFFDFFCC",
            "Light orange" to "#FFFFE6CC",
            "Light red" to "#FFFFD6D6"
        ),
        title = "Light theme background color",
        description = "Can be a hex color or a color resource reference.",
    )

    override fun execute(context: BytecodeContext) {
        StringsPatch.includePatchStrings("ThemeBytecode")
        SettingsPatch.PreferenceScreen.LAYOUT.addPreferences(
            SwitchPreference(
                "revanced_gradient_loading_screen",
                "revanced_gradient_loading_screen_title",
                "revanced_gradient_loading_screen_summary_on",
                "revanced_gradient_loading_screen_summary_off"
            )
        )

        UseGradientLoadingScreenFingerprint.result?.mutableMethod?.apply {
            val isEnabledIndex =
                indexOfFirstWideLiteralInstructionValue(GRADIENT_LOADING_SCREEN_AB_CONSTANT) + 3
            val isEnabledRegister =
                getInstruction<OneRegisterInstruction>(isEnabledIndex - 1).registerA

            addInstructions(
                isEnabledIndex,
                """
                    invoke-static { }, $INTEGRATIONS_CLASS_DESCRIPTOR->gradientLoadingScreenEnabled()Z
                    move-result v$isEnabledRegister
                """
            )
        } ?: throw UseGradientLoadingScreenFingerprint.exception

        LithoColorHookPatch.lithoColorOverrideHook(INTEGRATIONS_CLASS_DESCRIPTOR, "getValue")
    }
}
