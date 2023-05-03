package app.revanced.patches.youtube.layout.hide.endscreencards.resource.patch

import app.revanced.patcher.ResourceContext
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.patch.PatchException
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patches.shared.settings.preference.impl.StringResource
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.youtube.layout.hide.endscreencards.annotations.HideEndscreenCardsCompatibility
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch
import app.revanced.util.resources.ResourceUtils.resourceIdOf

@Name("hide-endscreen-cards-resource-patch")
@HideEndscreenCardsCompatibility
@DependsOn([SettingsPatch::class])
@Version("0.0.1")
class HideEndscreenCardsResourcePatch : ResourcePatch {
    internal companion object {
        var layoutCircle: Long = -1
        var layoutIcon: Long = -1
        var layoutVideo: Long = -1
    }

    override fun execute(context: ResourceContext): PatchResult {
        SettingsPatch.PreferenceScreen.LAYOUT.addPreferences(
            SwitchPreference(
                "revanced_hide_endscreen_cards",
                StringResource("revanced_hide_endscreen_cards_title", "Hide end screen cards"),
                true,
                StringResource("revanced_hide_endscreen_cards_summary_on", "End screen cards are hidden"),
                StringResource("revanced_hide_endscreen_cards_summary_off", "End screen cards are shown")
            ),
        )

        fun findEndscreenResourceId(name: String) = context.resourceIdOf("layout", "endscreen_element_layout_$name")

        layoutCircle = findEndscreenResourceId("circle")
        layoutIcon = findEndscreenResourceId("icon")
        layoutVideo = findEndscreenResourceId("video")

        return PatchResult.Success
    }
}