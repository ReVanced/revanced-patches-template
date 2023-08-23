package app.revanced.patches.youtube.layout.hide.endscreencards.resource.patch

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patches.shared.mapping.misc.patch.ResourceMappingPatch
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.youtube.misc.settings.bytecode.patch.YouTubeSettingsPatch

@DependsOn([YouTubeSettingsPatch::class, ResourceMappingPatch::class])
class HideEndscreenCardsResourcePatch : ResourcePatch {
    internal companion object {
        var layoutCircle: Long = -1
        var layoutIcon: Long = -1
        var layoutVideo: Long = -1
    }

    override fun execute(context: ResourceContext) {
        YouTubeSettingsPatch.PreferenceScreen.LAYOUT.addPreferences(
            SwitchPreference(
                "revanced_hide_endscreen_cards",
                "revanced_hide_endscreen_cards_title",
                "revanced_hide_endscreen_cards_summary_on",
                "revanced_hide_endscreen_cards_summary_off"
            ),
        )

        fun findEndscreenResourceId(name: String) = ResourceMappingPatch.resourceMappings.single {
            it.type == "layout" && it.name == "endscreen_element_layout_$name"
        }.id

        layoutCircle = findEndscreenResourceId("circle")
        layoutIcon = findEndscreenResourceId("icon")
        layoutVideo = findEndscreenResourceId("video")
    }
}