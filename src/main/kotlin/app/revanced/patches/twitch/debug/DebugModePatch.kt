package app.revanced.patches.twitch.debug

import app.revanced.extensions.exception
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.twitch.debug.fingerprints.IsDebugConfigEnabledFingerprint
import app.revanced.patches.twitch.debug.fingerprints.IsOmVerificationEnabledFingerprint
import app.revanced.patches.twitch.debug.fingerprints.ShouldShowDebugOptionsFingerprint
import app.revanced.patches.twitch.misc.integrations.IntegrationsPatch
import app.revanced.patches.twitch.misc.strings.StringsPatch
import app.revanced.patches.twitch.misc.settings.SettingsPatch

@Patch(
    name = "Debug mode",
    description = "Enables Twitch's internal debugging mode.",
    dependencies = [IntegrationsPatch::class, SettingsPatch::class],
    compatiblePackages = [CompatiblePackage("tv.twitch.android.app")],
    use = false
)
@Suppress("unused")
object DebugModePatch : BytecodePatch(
    setOf(
        IsDebugConfigEnabledFingerprint,
        IsOmVerificationEnabledFingerprint,
        ShouldShowDebugOptionsFingerprint
    )
) {
    override fun execute(context: BytecodeContext) {
        listOf(
            IsDebugConfigEnabledFingerprint,
            IsOmVerificationEnabledFingerprint,
            ShouldShowDebugOptionsFingerprint
        ).forEach {
            it.result?.mutableMethod?.apply {
                addInstructions(
                    0,
                    """
                         invoke-static {}, Lapp/revanced/twitch/patches/DebugModePatch;->isDebugModeEnabled()Z
                         move-result v0
                         return v0
                      """
                )
            } ?: throw it.exception
        }

        StringsPatch.includePatchStrings("DebugMode")
        SettingsPatch.PreferenceScreen.MISC.OTHER.addPreferences(
            SwitchPreference(
                "revanced_debug_mode",
                "revanced_debug_mode_title",
                "revanced_debug_mode_on",
                "revanced_debug_mode_off",
                default = false,
            )
        )
    }
}
