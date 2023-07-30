package app.revanced.patches.youtube.layout.hide.infocards.resource.patch

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patches.shared.mapping.misc.patch.ResourceMappingPatch
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.youtube.layout.hide.infocards.annotations.HideInfocardsCompatibility
import app.revanced.patches.youtube.misc.settings.bytecode.patch.YouTubeSettingsPatch

@HideInfocardsCompatibility
@DependsOn([YouTubeSettingsPatch::class, ResourceMappingPatch::class])
class HideInfocardsResourcePatch : ResourcePatch {
    internal companion object {
        var drawerResourceId: Long = -1
    }

    override fun execute(context: ResourceContext): PatchResult {
        YouTubeSettingsPatch.PreferenceScreen.LAYOUT.addPreferences(
            SwitchPreference(
                "revanced_hide_info_cards",
                "revanced_hide_info_cards_title",
                "revanced_hide_info_cards_summary_on",
                "revanced_hide_info_cards_summary_off"
            )
        )

        drawerResourceId = ResourceMappingPatch.resourceMappings.single {
            it.type == "id" && it.name == "info_cards_drawer_header"
        }.id

        return PatchResultSuccess()
    }
}