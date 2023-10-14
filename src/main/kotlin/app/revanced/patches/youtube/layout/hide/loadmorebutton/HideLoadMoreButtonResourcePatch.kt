package app.revanced.patches.youtube.layout.hide.loadmorebutton

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
    ]
)
object HideLoadMoreButtonResourcePatch : ResourcePatch() {
    internal var expandButtonDownId: Long = -1

    override fun execute(context: ResourceContext) {
        SettingsResourcePatch.includePatchStrings("HideLoadMoreButton")
        SettingsPatch.PreferenceScreen.LAYOUT.addPreferences(
            SwitchPreference(
                "revanced_hide_load_more_button",
                "revanced_hide_load_more_button_title",
                "revanced_hide_load_more_button_summary_on",
                "revanced_hide_load_more_button_summary_off",
            )
        )

        expandButtonDownId = ResourceMappingPatch.resourceMappings.single {
            it.type == "layout" && it.name == "expand_button_down"
        }.id
    }
}