package app.revanced.patches.youtube.video.videoqualitymenu.patch

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultError
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patches.shared.mapping.misc.patch.ResourceMappingPatch
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.youtube.misc.settings.bytecode.patch.YouTubeSettingsPatch

@DependsOn([YouTubeSettingsPatch::class, ResourceMappingPatch::class])
class OldVideoQualityMenuResourcePatch : ResourcePatch {
    override fun execute(context: ResourceContext): PatchResult {
        YouTubeSettingsPatch.PreferenceScreen.VIDEO.addPreferences(
            SwitchPreference(
                "revanced_show_old_video_quality_menu",
                "revanced_show_old_video_quality_menu_title",
                "revanced_show_old_video_quality_menu_summary_on",
                "revanced_show_old_video_quality_menu_summary_off"
            )
        )

        fun findResource(name: String) = ResourceMappingPatch.resourceMappings.find { it.name == name }?.id
            ?: throw PatchResultError("Could not find resource")

        // Used for the old type of the video quality menu.
        videoQualityBottomSheetListFragmentTitle = findResource("video_quality_bottom_sheet_list_fragment_title")

        // Used for the new type of the video quality menu.
        bottomSheetMargins = findResource("bottom_sheet_margins")

        return PatchResultSuccess()
    }

    internal companion object {
        var videoQualityBottomSheetListFragmentTitle = -1L
        var bottomSheetMargins = -1L
    }
}
