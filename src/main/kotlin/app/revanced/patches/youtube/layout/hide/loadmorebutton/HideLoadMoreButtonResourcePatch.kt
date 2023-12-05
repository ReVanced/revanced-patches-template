package app.revanced.patches.youtube.layout.hide.loadmorebutton

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patches.shared.mapping.misc.ResourceMappingPatch
import app.revanced.patches.shared.settings.preference.impl.StringResource
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.youtube.misc.settings.SettingsPatch

@Patch(
    dependencies = [
        SettingsPatch::class,
        ResourceMappingPatch::class
    ]
)
internal object HideLoadMoreButtonResourcePatch : ResourcePatch() {
    internal var expandButtonDownId: Long = -1

    override fun execute(context: ResourceContext) {
        SettingsPatch.PreferenceScreen.LAYOUT.addPreferences(
            SwitchPreference(
                "revanced_hide_load_more_button",
                StringResource("revanced_hide_load_more_button_title", "Hide \\\'Load More\\\' button"),
                StringResource("revanced_hide_load_more_button_summary_on", "Button is hidden"),
                StringResource("revanced_hide_load_more_button_summary_off", "Button is shown")
            )
        )

        expandButtonDownId = ResourceMappingPatch.resourceMappings.single {
            it.type == "layout" && it.name == "expand_button_down"
        }.id
    }
}