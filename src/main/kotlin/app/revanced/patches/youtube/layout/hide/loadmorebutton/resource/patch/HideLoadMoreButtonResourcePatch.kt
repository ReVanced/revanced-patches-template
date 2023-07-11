package app.revanced.patches.youtube.layout.hide.loadmorebutton.resource.patch

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patches.shared.mapping.misc.patch.ResourceMappingPatch
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.youtube.misc.settings.bytecode.patch.YouTubeSettingsPatch

@DependsOn([YouTubeSettingsPatch::class, ResourceMappingPatch::class])
class HideLoadMoreButtonResourcePatch : ResourcePatch {
    override fun execute(context: ResourceContext): PatchResult {
        YouTubeSettingsPatch.PreferenceScreen.LAYOUT.addPreferences(
            SwitchPreference(
                "revanced_hide_load_more_button",
                "revanced_hide_load_more_button_title",
                "revanced_hide_load_more_button_summary_on",
                "revanced_hide_load_more_button_summary_off"
            )
        )

        expandButtonDownId = ResourceMappingPatch.resourceMappings.single {
            it.type == "layout" && it.name == "expand_button_down"
        }.id

        return PatchResultSuccess()
    }

    internal companion object {
        var expandButtonDownId: Long = -1
    }
}