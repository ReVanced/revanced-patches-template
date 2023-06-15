package app.revanced.patches.youtube.layout.buttons.action.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
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
@Name("hide-video-action-buttons")
@Description("Adds the options to hide action buttons under a video.")
@HideButtonsCompatibility
@Version("0.0.1")
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
                        "revanced_hide_download_button",
                        "revanced_hide_download_button_title",
                        "revanced_hide_download_button_summary_on",
                        "revanced_hide_download_button_summary_off"
                    ),
                    SwitchPreference(
                        "revanced_hide_playlist_button",
                        "revanced_hide_playlist_button_title",
                        "revanced_hide_playlist_button_summary_on",
                        "revanced_hide_playlist_button_summary_off"
                    ),
                    SwitchPreference(
                        "revanced_hide_clip_button",
                        "revanced_hide_clip_button_title",
                        "revanced_hide_clip_button_summary_on",
                        "revanced_hide_clip_button_summary_off"
                    ),
                    SwitchPreference(
                        "revanced_hide_action_buttons",
                        "revanced_hide_action_buttons_title",
                        "revanced_hide_action_buttons_summary_on",
                        "revanced_hide_action_buttons_summary_off"
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
