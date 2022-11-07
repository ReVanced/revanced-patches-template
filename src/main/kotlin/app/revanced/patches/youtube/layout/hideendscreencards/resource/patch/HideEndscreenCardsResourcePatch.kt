package app.revanced.patches.youtube.layout.hideendscreencards.resource.patch

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patches.youtube.layout.hideendscreencards.annotations.HideEndScreenCardsCompatibility
import app.revanced.patches.youtube.misc.mapping.patch.ResourceMappingResourcePatch
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch
import app.revanced.patches.youtube.misc.settings.framework.components.impl.StringResource
import app.revanced.patches.youtube.misc.settings.framework.components.impl.SwitchPreference

@Name("hide-endscreen-cards-resource-patch")
@HideEndScreenCardsCompatibility
@DependsOn([SettingsPatch::class, ResourceMappingResourcePatch::class])
@Version("0.0.1")
class HideEndscreenCardsResourcePatch : ResourcePatch {
    companion object {
        internal var layoutCircle: Long = -1
        internal var layoutIcon: Long = -1
        internal var layoutVideo: Long = -1
    }

    override fun execute(context: ResourceContext): PatchResult {
        SettingsPatch.PreferenceScreen.LAYOUT.addPreferences(
            SwitchPreference(
                "revanced_endscreen_cards_removal",
                StringResource("revanced_endscreen_cards_removal_title", "Remove end-screen cards"),
                true,
                StringResource("revanced_endscreen_cards_removal_summary_on", "End-screen cards are hidden"),
                StringResource("revanced_endscreen_cards_removal_summary_off", "End-screen cards are shown")
            ),
        )

        layoutCircle = ResourceMappingResourcePatch.resourceMappings.single {
            it.type == "layout" && it.name == "endscreen_element_layout_circle"
        }.id
        layoutIcon = ResourceMappingResourcePatch.resourceMappings.single {
            it.type == "layout" && it.name == "endscreen_element_layout_icon"
        }.id
        layoutVideo = ResourceMappingResourcePatch.resourceMappings.single {
            it.type == "layout" && it.name == "endscreen_element_layout_video"
        }.id

        return PatchResultSuccess()
    }
}