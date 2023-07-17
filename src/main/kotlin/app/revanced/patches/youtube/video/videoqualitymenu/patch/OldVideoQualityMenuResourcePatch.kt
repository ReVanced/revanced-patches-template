package app.revanced.patches.youtube.video.videoqualitymenu.patch

import app.revanced.patcher.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patches.shared.settings.preference.impl.StringResource
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch
import app.revanced.util.resources.ResourceUtils.resourceIdOf

@DependsOn([SettingsPatch::class])
class OldVideoQualityMenuResourcePatch : ResourcePatch {
    override suspend fun execute(context: ResourceContext) {
        SettingsPatch.PreferenceScreen.VIDEO.addPreferences(
            SwitchPreference(
                "revanced_show_old_video_quality_menu",
                StringResource("revanced_show_old_video_quality_menu_title", "Show old video quality menu"),
                StringResource("revanced_show_old_video_quality_menu_summary_on", "Old video quality menu is shown"),
                StringResource("revanced_show_old_video_quality_menu_summary_off", "New video quality menu is hidden")
            )
        )

        // Used for the old type of the video quality menu.
        videoQualityBottomSheetListFragmentTitle = context.resourceIdOf("layout", "video_quality_bottom_sheet_list_fragment_title")
    }

    internal companion object {
        var videoQualityBottomSheetListFragmentTitle = -1L
    }
}
