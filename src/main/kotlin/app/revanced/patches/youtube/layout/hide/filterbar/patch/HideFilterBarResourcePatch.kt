package app.revanced.patches.youtube.layout.hide.filterbar.patch

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patches.shared.mapping.misc.patch.ResourceMappingPatch
import app.revanced.patches.shared.settings.preference.impl.PreferenceScreen
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.youtube.misc.settings.bytecode.patch.YouTubeSettingsPatch

@DependsOn([YouTubeSettingsPatch::class, ResourceMappingPatch::class])
class HideFilterBarResourcePatch : ResourcePatch {
    override fun execute(context: ResourceContext): PatchResult {
        YouTubeSettingsPatch.PreferenceScreen.LAYOUT.addPreferences(
            PreferenceScreen(
                "revanced_hide_filter_bar_preference",
                "revanced_hide_filter_bar_preference_title",
                listOf(
                    SwitchPreference(
                        "revanced_hide_filter_bar_feed_in_feed",
                        "revanced_hide_filter_bar_feed_in_feed_title",
                        "revanced_hide_filter_bar_feed_in_feed_summary_on",
                        "revanced_hide_filter_bar_feed_in_feed_summary_off"
                    ),
                    SwitchPreference(
                        "revanced_hide_filter_bar_feed_in_search",
                        "revanced_hide_filter_bar_feed_in_search_title",
                        "revanced_hide_filter_bar_feed_in_search_summary_on",
                        "revanced_hide_filter_bar_feed_in_search_summary_off"
                    ),
                    SwitchPreference(
                        "revanced_hide_filter_bar_feed_in_related_videos",
                        "revanced_hide_filter_bar_feed_in_related_videos_title",
                        "revanced_hide_filter_bar_feed_in_related_videos_summary_on",
                        "revanced_hide_filter_bar_feed_in_related_videos_summary_off"
                    ),
                ),
                "revanced_hide_filter_bar_preference_summary"
            )
        )

        relatedChipCloudMarginId = "related_chip_cloud_reduced_margins".layoutResourceId("layout")
        filterBarHeightId = "filter_bar_height".layoutResourceId()
        barContainerHeightId = "bar_container_height".layoutResourceId()

        return PatchResultSuccess()
    }

    internal companion object {
        var filterBarHeightId = -1L
        var relatedChipCloudMarginId = -1L
        var barContainerHeightId = -1L

        private fun String.layoutResourceId(type: String = "dimen") =
            ResourceMappingPatch.resourceMappings.single { it.type == type && it.name == this }.id
    }
}