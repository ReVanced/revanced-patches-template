package app.revanced.patches.youtube.layout.hide.breakingnews

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patches.shared.mapping.misc.ResourceMappingPatch
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.youtube.misc.settings.SettingsPatch
import app.revanced.patches.youtube.misc.settings.SettingsResourcePatch

@Patch(
    dependencies = [
        SettingsPatch::class,
        ResourceMappingPatch::class
    ],
)
object BreakingNewsResourcePatch : ResourcePatch() {
    internal var horizontalCardListId: Long = -1

    override fun execute(context: ResourceContext) {
        SettingsPatch.PreferenceScreen.LAYOUT.addPreferences(
            SwitchPreference(
                "revanced_hide_breaking_news",
                "revanced_hide_breaking_news_title",
                "revanced_hide_breaking_news_summary_on",
                "revanced_hide_breaking_news_summary_off"
            )
        )
        SettingsResourcePatch.mergePatchStrings("BreakingNews")

        horizontalCardListId = ResourceMappingPatch.resourceMappings.single {
            it.type == "layout" && it.name == "horizontal_card_list"
        }.id
    }
}