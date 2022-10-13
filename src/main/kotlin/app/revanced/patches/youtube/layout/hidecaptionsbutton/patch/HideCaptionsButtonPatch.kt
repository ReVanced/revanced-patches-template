package app.revanced.patches.youtube.layout.hidecaptionsbutton.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.extensions.instruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.util.smali.ExternalLabel
import app.revanced.patches.youtube.layout.autocaptions.annotations.AutoCaptionsCompatibility
import app.revanced.patches.youtube.layout.autocaptions.fingerprints.SubtitleButtonControllerFingerprint
import app.revanced.patches.youtube.layout.hidecaptionsbutton.fingerprints.CaptionsButtonOnClickFingerprint
import app.revanced.patches.youtube.layout.hidecaptionsbutton.fingerprints.CaptionsButtonOnLongClickFingerprint
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch
import app.revanced.patches.youtube.misc.settings.framework.components.impl.StringResource
import app.revanced.patches.youtube.misc.settings.framework.components.impl.SwitchPreference

@Patch
@DependsOn([IntegrationsPatch::class, SettingsPatch::class])
@Name("hide-captions-button-buttons")
@Description("Hide the captions button on video player.")
@AutoCaptionsCompatibility
@Version("0.0.1")
class HideCaptionsButtonPatch : BytecodePatch(listOf(
    SubtitleButtonControllerFingerprint,
    CaptionsButtonOnClickFingerprint,
    CaptionsButtonOnLongClickFingerprint,
)) {
    override fun execute(context: BytecodeContext): PatchResult {
        SettingsPatch.PreferenceScreen.LAYOUT.addPreferences(
            SwitchPreference(
                "revanced_hide_captions_button",
                StringResource("revanced_hide_captions_button_title", "Hide captions button"),
                false,
                StringResource("revanced_hide_captions_button_summary_on", "Captions button is hidden"),
                StringResource("revanced_hide_captions_button_summary_off", "Captions button is shown")
            )
        )

        val subtitleButtonControllerMethod = SubtitleButtonControllerFingerprint.result!!.mutableMethod
        subtitleButtonControllerMethod.addInstructions(
            0, """
                invoke-static {}, Lapp/revanced/integrations/patches/HideCaptionsButtonPatch;->hideCaptionsButton()Z
                move-result v0
                if-eqz v0, :hide_captions_button
                return-void
            """, listOf(ExternalLabel("hide_captions_button", subtitleButtonControllerMethod.instruction(0)))
        )

        val captionsButtonOnClickMethod = CaptionsButtonOnClickFingerprint.result!!.mutableMethod
        captionsButtonOnClickMethod.addInstructions(
            0, """
                invoke-static {}, Lapp/revanced/integrations/patches/HideCaptionsButtonPatch;->hideCaptionsButton()Z
                move-result v0
                if-eqz v0, :hide_captions_button
                return-void
            """, listOf(ExternalLabel("hide_captions_button", captionsButtonOnClickMethod.instruction(0)))
        )

        val captionsButtonOnLongClickMethod = CaptionsButtonOnLongClickFingerprint.result!!.mutableMethod
        captionsButtonOnLongClickMethod.addInstructions(
            0, """
                invoke-static {}, Lapp/revanced/integrations/patches/HideCaptionsButtonPatch;->hideCaptionsButton()Z
                move-result v0
                if-eqz v0, :hide_captions_button
                const/4 v0, 0x0
                return v0
            """, listOf(ExternalLabel("hide_captions_button", captionsButtonOnLongClickMethod.instruction(0)))
        )

        return PatchResultSuccess()
    }
}
