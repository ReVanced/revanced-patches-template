package app.revanced.patches.youtube.layout.buttons.action.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.shared.mapping.misc.patch.ResourceMappingPatch
import app.revanced.patches.shared.settings.preference.impl.PreferenceScreen
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.youtube.layout.buttons.action.annotations.HideButtonsCompatibility
import app.revanced.patches.youtube.misc.litho.filter.patch.LithoFilterPatch
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch

@Patch
@DependsOn([ResourceMappingPatch::class, LithoFilterPatch::class])
@Name("Hide video action buttons")
@Description("Adds the options to hide action buttons under a video.")
@HideButtonsCompatibility
class HideButtonsPatch : ResourcePatch {
    override fun execute(context: ResourceContext) {
        SettingsPatch.PreferenceScreen.LAYOUT.addPreferences(
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
                        "revanced_hide_live_chat_button_title",
                        "revanced_hide_live_chat_button_summary_on",
                        "revanced_hide_live_chat_button_summary_off",
                    ),
                    SwitchPreference(
                        "revanced_hide_share_button",
                        "revanced_hide_share_button_title",
                        "revanced_hide_share_button_summary_on",
                        "revanced_hide_share_button_summary_off"
                    ),
                    SwitchPreference(
                        "revanced_hide_report_button",
                        "revanced_hide_report_button_title",
                        "revanced_hide_report_button_summary_on",
                        "revanced_hide_report_button_summary_off",
                    ),
                    SwitchPreference(
                        "revanced_hide_remix_button",
                        "revanced_hide_remix_button_title",
                        "revanced_hide_remix_button_summary_on",
                        "revanced_hide_remix_button_summary_off",
                    ),
                    SwitchPreference(
                        "revanced_hide_download_button",
                        "revanced_hide_download_button_title",
                        "revanced_hide_download_button_summary_on",
                        "revanced_hide_download_button_summary_off"
                    ),
                    SwitchPreference(
                        "revanced_hide_thanks_button",
                        "revanced_hide_thanks_button_title",
                        "revanced_hide_thanks_button_summary_on",
                        "revanced_hide_thanks_button_summary_off",
                    ),
                    SwitchPreference(
                        "revanced_hide_clip_button",
                        "revanced_hide_clip_button_title",
                        "revanced_hide_clip_button_summary_on",
                        "revanced_hide_clip_button_summary_off",
                    ),
                    SwitchPreference(
                        "revanced_hide_playlist_button",
                        "revanced_hide_playlist_button_title",
                        "revanced_hide_playlist_button_summary_on",
                        "revanced_hide_playlist_button_summary_off",
                    ),
                    SwitchPreference(
                        "revanced_hide_shop_button",
                        "revanced_hide_shop_button_title",
                        "revanced_hide_shop_button_summary_on",
                        "revanced_hide_shop_button_summary_off",
                    )
                ),
                "revanced_hide_buttons_preference_screen_summary"
            )
        )

        LithoFilterPatch.addFilter(FILTER_CLASS_DESCRIPTOR)
    }

    private companion object {
        private const val FILTER_CLASS_DESCRIPTOR =
            "Lapp/revanced/integrations/patches/components/ButtonsFilter;"
    }
}
