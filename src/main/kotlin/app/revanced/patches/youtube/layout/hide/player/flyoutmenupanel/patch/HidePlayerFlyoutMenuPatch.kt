package app.revanced.patches.youtube.layout.hide.player.flyoutmenupanel.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.shared.settings.preference.impl.PreferenceScreen
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.youtube.layout.hide.player.flyoutmenupanel.annotations.HidePlayerFlyoutMenuItemsCompatibility
import app.revanced.patches.youtube.misc.litho.filter.patch.LithoFilterPatch
import app.revanced.patches.youtube.misc.settings.bytecode.patch.YouTubeSettingsPatch

@Patch
@Name("Player flyout menu")
@Description("Hides player flyout menu items.")
@DependsOn([LithoFilterPatch::class, YouTubeSettingsPatch::class])
@HidePlayerFlyoutMenuItemsCompatibility
class HidePlayerFlyoutMenuPatch : ResourcePatch {
    override fun execute(context: ResourceContext): PatchResult {
        YouTubeSettingsPatch.PreferenceScreen.LAYOUT.addPreferences(
            PreferenceScreen(
                "revanced_hide_player_flyout",
                "revanced_hide_player_flyout_title",
                listOf(
                    SwitchPreference(
                        "revanced_hide_player_flyout_quality",
                        "revanced_hide_player_flyout_quality_title",
                        "revanced_hide_player_flyout_quality_on",
                        "revanced_hide_player_flyout_quality_off",
                    ),
                    SwitchPreference(
                        "revanced_hide_player_flyout_captions",
                        "revanced_hide_player_flyout_captions_title",
                        "revanced_hide_player_flyout_captions_on",
                        "revanced_hide_player_flyout_captions_off",
                    ),
                    SwitchPreference(
                        "revanced_hide_player_flyout_loop_video",
                        "revanced_hide_player_flyout_loop_video_title",
                        "revanced_hide_player_flyout_loop_video_on",
                        "revanced_hide_player_flyout_loop_video_off",
                    ),
                    SwitchPreference(
                        "revanced_hide_player_flyout_ambient_mode",
                        "revanced_hide_player_flyout_ambient_mode_title",
                        "revanced_hide_player_flyout_ambient_mode_on",
                        "revanced_hide_player_flyout_ambient_mode_off",
                    ),
                    SwitchPreference(
                        "revanced_hide_player_flyout_report",
                        "revanced_hide_player_flyout_report_title",
                        "revanced_hide_player_flyout_report_on",
                        "revanced_hide_player_flyout_report_off",
                    ),
                    SwitchPreference(
                        "revanced_hide_player_flyout_help",
                        "revanced_hide_player_flyout_help_title",
                        "revanced_hide_player_flyout_help_on",
                        "revanced_hide_player_flyout_help_off",
                    ),
                    SwitchPreference(
                        "revanced_hide_player_flyout_speed",
                        "revanced_hide_player_flyout_speed_title",
                        "revanced_hide_player_flyout_speed_on",
                        "revanced_hide_player_flyout_speed_off",
                    ),
                    SwitchPreference(
                        "revanced_hide_player_flyout_more_info",
                        "revanced_hide_player_flyout_more_info_title",
                        "revanced_hide_player_flyout_more_info_on",
                        "revanced_hide_player_flyout_more_info_off",
                    ),
                    SwitchPreference(
                        "revanced_hide_player_flyout_audio_track",
                        "revanced_hide_player_flyout_audio_track_title",
                        "revanced_hide_player_flyout_audio_track_on",
                        "revanced_hide_player_flyout_audio_track_off",
                    ),
                    SwitchPreference(
                        "revanced_hide_player_flyout_watch_in_vr",
                        "revanced_hide_player_flyout_watch_in_vr_title",
                        "revanced_hide_player_flyout_watch_in_vr_on",
                        "revanced_hide_player_flyout_watch_in_vr_off",
                    ),
                ),
                "revanced_hide_player_flyout_summary",
            )
        )

        LithoFilterPatch.addFilter(FILTER_CLASS_DESCRIPTOR)

        return PatchResultSuccess()
    }

    private companion object {
        const val FILTER_CLASS_DESCRIPTOR =
            "Lapp/revanced/integrations/patches/components/PlayerFlyoutMenuItemsFilter;"
    }
}
