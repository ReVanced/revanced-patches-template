package app.revanced.patches.youtube.video.oldqualitylayout.patch

import app.revanced.patcher.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patches.shared.mapping.misc.patch.ResourceMappingPatch
import app.revanced.patches.shared.settings.preference.impl.StringResource
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch
import app.revanced.util.resources.ResourceUtils.resourceIdOf

@DependsOn([SettingsPatch::class, ResourceMappingPatch::class])
class OldQualityLayoutResourcePatch : ResourcePatch {
    override fun execute(context: ResourceContext) {
        SettingsPatch.PreferenceScreen.VIDEO.addPreferences(
            SwitchPreference(
                "revanced_show_old_video_menu",
                StringResource("revanced_show_old_video_menu_title", "Use old video quality player menu"),
                StringResource("revanced_show_old_video_menu_summary_on", "Old video quality menu is used"),
                StringResource("revanced_show_old_video_menu_summary_off", "Old video quality menu is not used")
            )
        )

        videoQualityBottomSheetListFragmentTitle = context.resourceIdOf("layout", "video_quality_bottom_sheet_list_fragment_title")
    }

    internal companion object {
        var videoQualityBottomSheetListFragmentTitle = -1L
    }
}
