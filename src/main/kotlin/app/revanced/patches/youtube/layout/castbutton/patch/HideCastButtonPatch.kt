package app.revanced.patches.youtube.layout.castbutton.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.impl.BytecodeData
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.patch.annotations.Dependencies
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.patch.impl.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patches.youtube.layout.castbutton.annotations.CastButtonCompatibility
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch
import app.revanced.patches.youtube.misc.settings.framework.components.impl.StringResource
import app.revanced.patches.youtube.misc.settings.framework.components.impl.SwitchPreference

@Patch
@Dependencies([IntegrationsPatch::class])
@Name("hide-cast-button")
@Description("Hides the cast button in the video player.")
@CastButtonCompatibility
@Version("0.0.1")
class HideCastButtonPatch : BytecodePatch() {
    override fun execute(data: BytecodeData): PatchResult {
        SettingsPatch.PreferenceScreen.LAYOUT.addPreferences(
            SwitchPreference(
                "revanced_cast_button_enabled",
                StringResource("revanced_cast_button_enabled_title", "Show cast button"),
                false,
                StringResource("revanced_cast_button_summary_on", "Cast button is shown."),
                StringResource("revanced_cast_button_summary_off", "Cast button is hidden.")
            )
        )

        data.classes.forEach { classDef ->
            classDef.methods.forEach { method ->
                if (classDef.type.endsWith("MediaRouteButton;") && method.name == "setVisibility") {
                    val setVisibilityMethod =
                        data.proxy(classDef).resolve().methods.first { it.name == "setVisibility" }

                    setVisibilityMethod.addInstructions(
                        0, """
                            invoke-static {p1}, Lapp/revanced/integrations/patches/HideCastButtonPatch;->getCastButtonOverrideV2(I)I
                            move-result p1
                        """
                    )
                }
            }
        }

        return PatchResultSuccess()
    }
}
