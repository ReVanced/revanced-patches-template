package app.revanced.patches.youtube.layout.buttons.action.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.shared.mapping.misc.patch.ResourceMappingPatch
import app.revanced.patches.shared.settings.preference.impl.PreferenceScreen
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.youtube.layout.buttons.action.annotations.HideButtonsCompatibility
import app.revanced.patches.youtube.misc.litho.filter.patch.LithoFilterPatch
import app.revanced.patches.youtube.misc.settings.bytecode.patch.YouTubeSettingsPatch

@Patch
@DependsOn([ResourceMappingPatch::class, LithoFilterPatch::class])
@Name("Hide video action buttons")
@Description("Adds the options to hide action buttons under a video.")
@HideButtonsCompatibility
class HideButtonsPatch : ResourcePatch {
    override fun execute(context: ResourceContext): PatchResult {
        YouTubeSettingsPatch.PreferenceScreen.LAYOUT.addPreferences(
            PreferenceScreen(
                "revanced_hide_buttons_preference_screen",
                "revanced_hide_buttons_preference_screen_title",
                listOf(
                    SwitchPreference(
                        "revanced_hide_like_dislike_button",
                        "revanced_hide_like_dislike_button_title",
                        "revanced_hide_like_dislike_button_summary_on",
                        "revanced_hide_like_dislike_button_summary_off"
                    ),
                    SwitchPreference(
                        "revanced_hide_live_chat_button",
                        StringResource("revanced_hide_live_chat_button_title", "Hide live chat button"),
                        StringResource("revanced_hide_live_chat_button_summary_on", "Live chat button is hidden"),
                        StringResource("revanced_hide_live_chat_button_summary_off", "Live chat button is shown")
                    ),
                    SwitchPreference(
                        "revanced_hide_share_button",
                        StringResource("revanced_hide_share_button_title", "Hide share button"),
                        StringResource("revanced_hide_share_button_summary_on", "Share button is hidden"),
                        StringResource("revanced_hide_share_button_summary_off", "Share button is shown")
                    ),
                    SwitchPreference(
                        "revanced_hide_report_button",
                        StringResource("revanced_hide_report_button_title", "Hide report button"),
                        StringResource("revanced_hide_report_button_summary_on", "Report button is hidden"),
                        StringResource("revanced_hide_report_button_summary_off", "Report button is shown")
                    ),
                    SwitchPreference(
                        "revanced_hide_remix_button",
                        StringResource("revanced_hide_remix_button_title", "Hide remix button"),
                        StringResource("revanced_hide_remix_button_summary_on", "Remix button is hidden"),
                        StringResource("revanced_hide_remix_button_summary_off", "Remix button is shown")
                    ),
                    SwitchPreference(
                        "revanced_hide_download_button",
                        "revanced_hide_download_button_title",
                        "revanced_hide_download_button_summary_on",
                        "revanced_hide_download_button_summary_off"
                    ),
                    SwitchPreference(
                        "revanced_hide_playlist_button",
                        StringResource("revanced_hide_playlist_button_title", "Hide playlist button"),
                        StringResource("revanced_hide_playlist_button_summary_on", "Playlist button is hidden"),
                        StringResource("revanced_hide_playlist_button_summary_off", "Playlist button is shown")
                    ),
                    SwitchPreference(
                        "revanced_hide_clip_button",
                        StringResource("revanced_hide_clip_button_title", "Hide clip button"),
                        StringResource("revanced_hide_clip_button_summary_on", "Clip button is hidden"),
                        StringResource("revanced_hide_clip_button_summary_off", "Clip button is shown"),
                        StringResource("revanced_hide_clip_button_user_dialog_message",
                            "Hiding the clip button might not work reliably. In the case it does not work, it can only be hidden by enabling \\'Hide all other action buttons\\'")
                    ),
                    SwitchPreference(
                        "revanced_hide_action_buttons",
                        StringResource("revanced_hide_action_buttons_title", "Hide all other action buttons"),
                        StringResource("revanced_hide_action_buttons_summary_on", "Share, remix, thanks, shop, live chat buttons are hidden"),
                        StringResource("revanced_hide_action_buttons_summary_off", "Share, remix, thanks, shop, live chat buttons are shown")
                    )
                ),
                "revanced_hide_buttons_preference_screen_summary"
            )
        )

        LithoFilterPatch.addFilter(FILTER_CLASS_DESCRIPTOR)

        return PatchResultSuccess()
    }

    private companion object {
        private const val FILTER_CLASS_DESCRIPTOR =
            "Lapp/revanced/integrations/patches/components/ButtonsFilter;"
    }
}
