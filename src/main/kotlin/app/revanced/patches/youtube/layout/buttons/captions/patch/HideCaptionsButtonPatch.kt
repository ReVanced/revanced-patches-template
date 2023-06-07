package app.revanced.patches.youtube.layout.buttons.captions.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.shared.settings.preference.impl.StringResource
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.youtube.layout.autocaptions.fingerprints.SubtitleButtonControllerFingerprint
import app.revanced.patches.youtube.layout.buttons.captions.annotations.HideCaptionsButtonCompatibility
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch
import org.jf.dexlib2.Opcode

@Patch
@DependsOn([IntegrationsPatch::class, SettingsPatch::class])
@Name("hide-captions-button")
@Description("Hides the captions button on video player.")
@HideCaptionsButtonCompatibility
@Version("0.0.1")
class HideCaptionsButtonPatch : BytecodePatch(listOf(
    SubtitleButtonControllerFingerprint,
)) {
    override fun execute(context: BytecodeContext): PatchResult {
        SettingsPatch.PreferenceScreen.LAYOUT.addPreferences(
            SwitchPreference(
                "revanced_hide_captions_button",
                StringResource("revanced_hide_captions_button_title", "Hide captions button"),
                StringResource("revanced_hide_captions_button_summary_on", "Captions button is hidden"),
                StringResource("revanced_hide_captions_button_summary_off", "Captions button is shown")
            )
        )

        val subtitleButtonControllerMethod = SubtitleButtonControllerFingerprint.result!!.mutableMethod

        // Due to previously applied patches, scanResult index cannot be used in this context
        val igetBooleanIndex = subtitleButtonControllerMethod.implementation!!.instructions.indexOfFirst {
            it.opcode == Opcode.IGET_BOOLEAN
        }

        subtitleButtonControllerMethod.addInstruction(
            igetBooleanIndex + 1,
            """
                invoke-static {v0}, Lapp/revanced/integrations/patches/HideCaptionsButtonPatch;->hideCaptionsButton(Landroid/widget/ImageView;)V
            """
        )

        return PatchResultSuccess()
    }
}
