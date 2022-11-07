package app.revanced.patches.youtube.ad.infocardsuggestions.resource.patch

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patches.youtube.ad.infocardsuggestions.annotations.HideInfocardSuggestionsCompatibility
import app.revanced.patches.youtube.misc.mapping.patch.ResourceMappingResourcePatch
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch
import app.revanced.patches.youtube.misc.settings.framework.components.impl.StringResource
import app.revanced.patches.youtube.misc.settings.framework.components.impl.SwitchPreference

@Name("hide-infocard-suggestion-resource")
@HideInfocardSuggestionsCompatibility
@DependsOn([SettingsPatch::class, ResourceMappingResourcePatch::class])
@Version("0.0.1")
class HideInfocardSuggestionsResourcePatch : ResourcePatch {
    companion object {
        internal var drawerResourceId: Long = -1
    }

    override fun execute(context: ResourceContext): PatchResult {
        SettingsPatch.PreferenceScreen.ADS.addPreferences(
            SwitchPreference(
                "revanced_info_cards_enabled",
                StringResource("revanced_info_cards_enabled_title", "Show info-cards"),
                false,
                StringResource("revanced_info_cards_enabled_summary_on", "Info-cards are shown"),
                StringResource("revanced_info_cards_enabled_summary_off", "Info-cards are hidden")
            )
        )

        drawerResourceId = ResourceMappingResourcePatch.resourceMappings.single {
            it.type == "id" && it.name == "info_cards_drawer_header"
        }.id

        return PatchResultSuccess()
    }
}