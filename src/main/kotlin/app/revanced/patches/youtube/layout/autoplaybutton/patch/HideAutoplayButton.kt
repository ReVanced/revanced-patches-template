package app.revanced.patches.youtube.layout.autoplaybutton.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.impl.BytecodeData
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.patch.impl.BytecodePatch
import app.revanced.patcher.util.smali.ExternalLabel
import app.revanced.patches.youtube.layout.autoplaybutton.annotations.AutoplayButtonCompatibility
import app.revanced.patches.youtube.layout.autoplaybutton.fingerprints.AutoNavInformerFingerprint
import app.revanced.patches.youtube.layout.autoplaybutton.fingerprints.LayoutConstructorFingerprint
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch
import app.revanced.patches.youtube.misc.settings.framework.components.impl.StringResource
import app.revanced.patches.youtube.misc.settings.framework.components.impl.SwitchPreference
import org.jf.dexlib2.iface.instruction.Instruction

@Patch
@DependsOn([IntegrationsPatch::class, SettingsPatch::class])
@Name("hide-autoplay-button")
@Description("Hides the autoplay button in the video player.")
@AutoplayButtonCompatibility
@Version("0.0.1")
class HideAutoplayButton : BytecodePatch(
    listOf(
        LayoutConstructorFingerprint, AutoNavInformerFingerprint
    )
) {
    override fun execute(data: BytecodeData): PatchResult {
        SettingsPatch.PreferenceScreen.LAYOUT.addPreferences(
            SwitchPreference(
                "revanced_autoplay_button_enabled",
                StringResource("revanced_autoplay_button_enabled_title", "Show autoplay button"),
                false,
                StringResource("revanced_autoplay_button_summary_on", "Autoplay button is shown."),
                StringResource("revanced_autoplay_button_summary_off", "Autoplay button is hidden.")
            )
        )

        val autoNavInformerMethod = AutoNavInformerFingerprint.result!!.mutableMethod

        val layoutGenMethodResult = LayoutConstructorFingerprint.result!!
        val layoutGenMethod = layoutGenMethodResult.mutableMethod
        val layoutGenMethodInstructions = layoutGenMethod.implementation!!.instructions

        val scanStartIndex = layoutGenMethodResult.patternScanResult!!.startIndex
        val relativeOffset = 12 // offset to the instruction that is being jumped to
        val jumpInstruction = layoutGenMethodInstructions[scanStartIndex + relativeOffset] as Instruction

        layoutGenMethod.addInstructions(
            scanStartIndex, """
                invoke-static {}, Lapp/revanced/integrations/patches/HideAutoplayButtonPatch;->isButtonShown()Z
                move-result v11
                if-eqz v11, :hidden
            """, listOf(ExternalLabel("hidden", jumpInstruction))
        )

        //force disable autoplay since it's hard to do without the button
        autoNavInformerMethod.addInstructions(
            0, """
            invoke-static {}, Lapp/revanced/integrations/patches/HideAutoplayButtonPatch;->isButtonShown()Z
            move-result v0
            if-nez v0, :hidden
            const/4 v0, 0x0
            return v0
            :hidden
            nop
        """
        )

        return PatchResultSuccess()
    }
}
