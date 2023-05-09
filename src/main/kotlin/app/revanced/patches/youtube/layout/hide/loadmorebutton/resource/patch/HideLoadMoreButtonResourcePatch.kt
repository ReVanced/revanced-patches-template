package app.revanced.patches.youtube.layout.hide.loadmorebutton.resource.patch

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patches.shared.mapping.misc.patch.ResourceMappingPatch
import app.revanced.patches.shared.settings.preference.impl.StringResource
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.youtube.layout.hide.loadmorebutton.annotations.HideLoadMoreButtonCompatibility
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch
import app.revanced.util.resources.ResourceUtils.resourceIdOf

@Name("hide-load-more-button-resource-patch")
@DependsOn([SettingsPatch::class, ResourceMappingPatch::class])
@HideLoadMoreButtonCompatibility
class HideLoadMoreButtonResourcePatch : ResourcePatch {
    override fun execute(context: ResourceContext) {
        SettingsPatch.PreferenceScreen.LAYOUT.addPreferences(
            SwitchPreference(
                "revanced_hide_load_more_button",
                StringResource("revanced_hide_load_more_button_title", "Hide Load More button"),
                true,
                StringResource("revanced_hide_load_more_button_summary_on", "Load More button is hidden"),
                StringResource("revanced_hide_load_more_button_summary_off", "Load More button is shown")
            )
        )

        expandButtonDownId = context.resourceIdOf("layout", "expand_button_down")
    }

    internal companion object {
        var expandButtonDownId: Long = -1
    }
}