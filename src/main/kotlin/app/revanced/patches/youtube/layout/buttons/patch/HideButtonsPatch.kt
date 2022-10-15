package app.revanced.patches.youtube.layout.buttons.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.youtube.ad.general.bytecode.patch.GeneralBytecodeAdsPatch
import app.revanced.patches.youtube.layout.buttons.annotations.HideButtonsCompatibility
import app.revanced.patches.youtube.misc.mapping.patch.ResourceMappingResourcePatch
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch
import app.revanced.patches.youtube.misc.settings.framework.components.impl.PreferenceScreen
import app.revanced.patches.youtube.misc.settings.framework.components.impl.StringResource
import app.revanced.patches.youtube.misc.settings.framework.components.impl.SwitchPreference

@Patch
@DependsOn([ResourceMappingResourcePatch::class, GeneralBytecodeAdsPatch::class])
@Name("hide-video-buttons")
@Description("Adds options to hide action buttons under a video.")
@HideButtonsCompatibility
@Version("0.0.1")
class HideButtonsPatch : ResourcePatch {
    override fun execute(context: ResourceContext): PatchResult {
        SettingsPatch.PreferenceScreen.LAYOUT.addPreferences(
            SwitchPreference(
                "revanced_action_bar",
                StringResource("revanced_action_bar_title", "Hide action bar"),
                false,
                StringResource("revanced_action_bar_on", "Action bar is hidden"),
                StringResource("revanced_action_bar_off", "Action bar is shown")
            ),
            PreferenceScreen(
                "revanced_hide_buttons",
                StringResource("revanced_hide_buttons_title", "Hide action buttons"),
                listOf(
                    SwitchPreference(
                        "revanced_like_button",
                        StringResource("revanced_like_button_title", "Hide like button"),
                        false,
                        StringResource("revanced_like_button_on", "Like button is hidden"),
                        StringResource("revanced_like_button_off", "Like button is shown")
                    ),
                    SwitchPreference(
                        "revanced_dislike_button",
                        StringResource("revanced_dislike_button_title", "Hide dislike button"),
                        false,
                        StringResource("revanced_dislike_button_on", "Dislike button is hidden"),
                        StringResource("revanced_dislike_button_off", "Dislike button is shown")
                    ),
                    SwitchPreference(
                        "revanced_share_button",
                        StringResource("revanced_share_button_title", "Hide share button"),
                        false,
                        StringResource("revanced_share_button_on", "Share button is hidden"),
                        StringResource("revanced_share_button_off", "Share button is shown")
                    ),
                    SwitchPreference(
                        "revanced_live_chat_button",
                        StringResource("revanced_live_chat_button_title", "Hide live chat button"),
                        false,
                        StringResource("revanced_live_chat_button_on", "Live chat button is hidden"),
                        StringResource("revanced_live_chat_button_off", "Live chat button is shown")
                    ),
                    SwitchPreference(
                        "revanced_report_button",
                        StringResource("revanced_report_button_title", "Hide report button"),
                        false,
                        StringResource("revanced_report_button_on", "Report button is hidden"),
                        StringResource("revanced_report_button_off", "Report button is shown")
                    ),
                    SwitchPreference(
                        "revanced_create_shorts_button",
                        StringResource("revanced_create_shorts_button_title", "Hide create shorts button"),
                        true,
                        StringResource("revanced_create_shorts_button_on", "Create shorts button is hidden"),
                        StringResource("revanced_create_shorts_button_off", "Create shorts button is shown")
                    ),
                    SwitchPreference(
                        "revanced_thanks_button",
                        StringResource("revanced_thanks_button_title", "Hide thanks button"),
                        true,
                        StringResource("revanced_thanks_button_on", "Thanks button is hidden"),
                        StringResource("revanced_thanks_button_off", "Thanks button is shown")
                    ),
                    SwitchPreference(
                        "revanced_create_clip_button",
                        StringResource("revanced_create_clip_button_title", "Hide clip button"),
                        true,
                        StringResource("revanced_create_clip_button_on", "Clip button is hidden"),
                        StringResource("revanced_create_clip_button_off", "Clip button is shown")
                    ),
                    SwitchPreference(
                        "revanced_download_button",
                        StringResource("revanced_download_button_title", "Hide download button"),
                        false,
                        StringResource("revanced_download_button_on", "Download button is hidden"),
                        StringResource("revanced_download_button_off", "Download button is shown")
                    ),
                    SwitchPreference(
                        "revanced_playlist_button",
                        StringResource("revanced_playlist_button_title", "Hide playlist button"),
                        false,
                        StringResource("revanced_playlist_button_on", "Playlist button is hidden"),
                        StringResource("revanced_playlist_button_off", "Playlist button is shown")
                    ),

                ),
                StringResource("revanced_hide_buttons_summary", "Hide or show buttons under videos")
            )
        )
        return PatchResultSuccess()
    }
}
