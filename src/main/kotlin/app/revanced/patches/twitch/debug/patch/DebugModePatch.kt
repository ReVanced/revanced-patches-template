package app.revanced.patches.twitch.debug.patch

import app.revanced.extensions.exception
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.twitch.debug.annotations.DebugModeCompatibility
import app.revanced.patches.twitch.debug.fingerprints.IsDebugConfigEnabledFingerprint
import app.revanced.patches.twitch.debug.fingerprints.IsOmVerificationEnabledFingerprint
import app.revanced.patches.twitch.debug.fingerprints.ShouldShowDebugOptionsFingerprint
import app.revanced.patches.twitch.misc.integrations.patch.IntegrationsPatch
import app.revanced.patches.twitch.misc.settings.bytecode.patch.SettingsPatch

@Patch(false)
@DependsOn([IntegrationsPatch::class, SettingsPatch::class])
@Name("Debug mode")
@Description("Enables Twitch's internal debugging mode.")
@DebugModeCompatibility
class DebugModePatch : BytecodePatch(
    listOf(
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

        SettingsPatch.PreferenceScreen.MISC.OTHER.addPreferences(
            SwitchPreference(
                "revanced_debug_mode",
                "revanced_debug_mode_enable",
                "revanced_debug_mode_on",
                "revanced_debug_mode_off",
                default = false,
            )
        )
    }
}
