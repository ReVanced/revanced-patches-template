package app.revanced.patches.youtube.layout.buttons.cast.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchException
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.youtube.layout.buttons.cast.annotations.CastButtonCompatibility
import app.revanced.patches.youtube.misc.integrations.patch.YouTubeIntegrationsPatch
import app.revanced.patches.youtube.misc.settings.bytecode.patch.YouTubeSettingsPatch

@Patch
@DependsOn([YouTubeIntegrationsPatch::class, YouTubeSettingsPatch::class])
@Name("Hide cast button")
@Description("Hides the cast button in the video player.")
@CastButtonCompatibility
class HideCastButtonPatch : BytecodePatch() {
    override fun execute(context: BytecodeContext) {
        YouTubeSettingsPatch.PreferenceScreen.LAYOUT.addPreferences(
            SwitchPreference(
                "revanced_hide_cast_button",
                "revanced_hide_cast_button_title",
                "revanced_hide_cast_button_summary_on",
                "revanced_hide_cast_button_summary_off"
            )
        )

        val buttonClass = context.findClass("MediaRouteButton")
            ?: throw PatchException("MediaRouteButton class not found.")

        buttonClass.mutableClass.methods.find { it.name == "setVisibility" }?.apply {
            addInstructions(
                0,
                """
                    invoke-static {p1}, Lapp/revanced/integrations/patches/HideCastButtonPatch;->getCastButtonOverrideV2(I)I
                    move-result p1
                """
            )
        } ?: throw PatchException("setVisibility method not found.")
    }
}
