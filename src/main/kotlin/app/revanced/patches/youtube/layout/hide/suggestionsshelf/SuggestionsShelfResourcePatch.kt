package app.revanced.patches.youtube.layout.hide.suggestionsshelf

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patches.shared.mapping.misc.ResourceMappingPatch
import app.revanced.patches.shared.settings.preference.impl.StringResource
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.youtube.misc.settings.SettingsPatch

@Patch(dependencies = [ResourceMappingPatch::class])
object SuggestionsShelfResourcePatch: ResourcePatch() {
    internal var horizontalCardListId = -1L

    override fun execute(context: ResourceContext) {
        SettingsPatch.PreferenceScreen.LAYOUT.addPreferences(
            SwitchPreference(
                "revanced_hide_suggestions_shelf",
                StringResource("revanced_hide_suggestions_shelf_title", "Hide Suggestions shelves"),
                StringResource("revanced_hide_suggestions_shelf_on", "Suggestions shelves are hidden"),
                StringResource("revanced_hide_suggestions_shelf_off", "Suggestions shelves are shown")
            )
        )

        horizontalCardListId = ResourceMappingPatch.resourceMappings.single {
            it.type == "layout" && it.name == "horizontal_card_list"
        }.id
    }
}