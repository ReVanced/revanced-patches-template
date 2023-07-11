package app.revanced.patches.youtube.layout.hide.breakingnews.resource.patch

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patches.shared.mapping.misc.patch.ResourceMappingPatch
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.youtube.misc.settings.bytecode.patch.YouTubeSettingsPatch

@DependsOn([YouTubeSettingsPatch::class, ResourceMappingPatch::class])
class BreakingNewsResourcePatch : ResourcePatch {
    companion object {
        internal var horizontalCardListId: Long = -1
    }

    override fun execute(context: ResourceContext): PatchResult {
        YouTubeSettingsPatch.PreferenceScreen.LAYOUT.addPreferences(
            SwitchPreference(
                "revanced_hide_breaking_news",
                "revanced_hide_breaking_news_title",
                "revanced_hide_breaking_news_summary_on",
                "revanced_hide_breaking_news_summary_off"
            )
        )

        horizontalCardListId = ResourceMappingPatch.resourceMappings.single {
            it.type == "layout" && it.name == "horizontal_card_list"
        }.id

        return PatchResultSuccess()
    }
}