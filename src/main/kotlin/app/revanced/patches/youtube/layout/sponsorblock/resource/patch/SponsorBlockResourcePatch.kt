package app.revanced.patches.youtube.layout.sponsorblock.resource.patch

import app.revanced.patcher.ResourceContext
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patches.shared.mapping.misc.patch.ResourceMappingPatch
import app.revanced.patches.shared.settings.preference.impl.Preference
import app.revanced.patches.shared.settings.preference.impl.StringResource
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch
import app.revanced.util.resources.ResourceUtils
import app.revanced.util.resources.ResourceUtils.base
import app.revanced.util.resources.ResourceUtils.copyResources
import app.revanced.util.resources.ResourceUtils.copyXmlNode
import app.revanced.util.resources.ResourceUtils.mergeStrings

@Name("sponsorblock-resource-patch")
@DependsOn([SettingsPatch::class, ResourceMappingPatch::class])
@Version("0.0.1")
class SponsorBlockResourcePatch : ResourcePatch {
    private companion object {
        val strings = mapOf(
            "sb_enable_sb" to "Enable SponsorBlock",
            "sb_enable_sb_sum" to "SponsorBlock is a crowd-sourced system for skipping annoying parts of YouTube videos",

            "sb_appearance_category" to "Appearance",
            "sb_enable_voting" to "Show voting button",
            "sb_enable_voting_sum_on" to "Segment voting button is shown",
            "sb_enable_voting_sum_off" to "Segment voting button is not shown",
            "sb_enable_compact_skip_button" to "Use compact skip button",
            "sb_enable_compact_skip_button_sum_on" to "Skip button styled for minimum width",
            "sb_enable_compact_skip_button_sum_off" to "Skip button styled for best appearance",
            "sb_enable_auto_hide_skip_segment_button" to "Automatically hide skip button",
            "sb_enable_auto_hide_skip_segment_button_sum_on" to "Skip button hides after a few seconds",
            "sb_enable_auto_hide_skip_segment_button_sum_off" to "Skip button displayed for entire segment",
            "sb_general_skiptoast" to "Show a toast when skipping automatically",
            "sb_general_skiptoast_sum_on" to "Toast shown when a segment is automatically skipped. Tap here to see an example",
            "sb_general_skiptoast_sum_off" to "Toast not shown. Tap here to see an example",
            "sb_general_time_without" to "Show video length without segments",
            "sb_general_time_without_sum_on" to "Video length minus all segments, shown in parentheses next to the full video length",
            "sb_general_time_without_sum_off" to "Full video length shown",

            "sb_create_segment_category" to "Creating new segments",
            "sb_enable_create_segment" to "Show create new segment button",
            "sb_enable_create_segment_sum_on" to "Create new segment button is shown",
            "sb_enable_create_segment_sum_off" to "Create new segment button is not shown",
            "sb_general_adjusting" to "Adjust new segment step",
            "sb_general_adjusting_sum" to "Number of milliseconds the time adjustment buttons move when creating new segments",
            "sb_general_adjusting_invalid" to "Value must be a positive number",
            "sb_guidelines_preference_title" to "View guidelines",
            "sb_guidelines_preference_sum" to "Guidelines contain rules and tips for creating new segments",
            "sb_guidelines_popup_title" to "Follow the guidelines",
            "sb_guidelines_popup_content" to "Read the SponsorBlock guidelines before creating new segments",
            "sb_guidelines_popup_already_read" to "Already read",
            "sb_guidelines_popup_open" to "Show me",

            "sb_general" to "General",
            "sb_general_skipcount" to "Enable skip count tracking",
            "sb_general_skipcount_sum_on" to "Lets the SponsorBlock leaderboard know how much time is saved. A message is sent to the leaderboard each time a segment is skipped",
            "sb_general_skipcount_sum_off" to "Skip count tracking is not enabled",
            "sb_general_min_duration" to "Minimum segment duration",
            "sb_general_min_duration_sum" to "Segments shorter than this value (in seconds) will not be shown or skipped",
            "sb_general_uuid" to "Your private user id",
            "sb_general_uuid_sum" to "This should be kept private. This is like a password and should not be shared with anyone. If someone has this, they can impersonate you",
            "sb_general_uuid_invalid" to "User id cannot be blank",
            "sb_general_api_url" to "Change API URL",
            "sb_general_api_url_sum" to "The address SponsorBlock uses to make calls to the server. Do not change this unless you know what you\'re doing",
            "sb_api_url_reset" to "API URL reset",
            "sb_api_url_invalid" to "API URL is invalid",
            "sb_api_url_changed" to "API URL changed",
            "sb_settings_ie" to "Import/Export settings",
            "sb_settings_ie_sum" to "Your SponsorBlock JSON configuration that can be imported/exported to ReVanced and other SponsorBlock platforms. This includes your private user id. Be sure to share this wisely",
            "sb_settings_import_successful" to "Settings imported successfully",
            "sb_settings_import_failed" to "Failed to import: %s",
            "sb_settings_export_failed" to "Failed to export settings (try clearing app data)",

            "sb_diff_segments" to "Change segment behavior",
            "sb_segments_sponsor" to "Sponsor",
            "sb_segments_sponsor_sum" to "Paid promotion, paid referrals and direct advertisements. Not for self-promotion or free shout-outs to causes/creators/websites/products they like",
            "sb_segments_selfpromo" to "Unpaid/Self Promotion",
            "sb_segments_selfpromo_sum" to "Similar to \'Sponsor\' except for unpaid or self promotion. Includes sections about merchandise, donations, or information about who they collaborated with",
            "sb_segments_interaction" to "Interaction Reminder (Subscribe)",
            "sb_segments_interaction_sum" to "A short reminder to like, subscribe or follow them in the middle of content. If it is long or about something specific, it should instead be under self promotion",
            "sb_segments_highlight" to "Highlight",
            "sb_segments_highlight_sum" to "The part of the video that most people are looking for",
            "sb_segments_intro" to "Intermission/Intro Animation",
            "sb_segments_intro_sum" to "An interval without actual content. Could be a pause, static frame, or repeating animation. Does not include transitions containing information",
            "sb_segments_outro" to "Endcards/Credits",
            "sb_segments_outro_sum" to "Credits or when the YouTube endcards appear. Not for conclusions with information",
            "sb_segments_preview" to "Preview/Recap",
            "sb_segments_preview_sum" to "Collection of clips that show what is coming up or what happened in the video or in other videos of a series, where all information is repeated elsewhere",
            "sb_segments_filler" to "Filler Tangent/Jokes",
            "sb_segments_filler_sum" to "Tangential scenes added only for filler or humor that are not required to understand the main content of the video. Does not include segments providing context or background details",
            "sb_segments_nomusic" to "Music: Non-Music Section",
            "sb_segments_nomusic_sum" to "Only for use in music videos. Sections of music videos without music, that aren\'t already covered by another category",

            "sb_skip_button_compact" to "Skip",
            "sb_skip_button_compact_highlight" to "Highlight",
            "sb_skip_button_sponsor" to "Skip sponsor",
            "sb_skip_button_selfpromo" to "Skip promo",
            "sb_skip_button_interaction" to "Skip interact",
            "sb_skip_button_highlight" to "Skip to highlight",
            "sb_skip_button_intro_beginning" to "Skip intro",
            "sb_skip_button_intro_middle" to "Skip intermission",
            "sb_skip_button_intro_end" to "Skip intermission",
            "sb_skip_button_outro" to "Skip outro",
            "sb_skip_button_preview_beginning" to "Skip preview",
            "sb_skip_button_preview_middle" to "Skip preview",
            "sb_skip_button_preview_end" to "Skip recap",
            "sb_skip_button_filler" to "Skip filler",
            "sb_skip_button_nomusic" to "Skip non-music",
            "sb_skip_button_unsubmitted" to "Skip segment",

            "sb_skipped_sponsor" to "Skipped sponsor",
            "sb_skipped_selfpromo" to "Skipped self promotion",
            "sb_skipped_interaction" to "Skipped annoying reminder",
            "sb_skipped_highlight" to "Skipped to highlight",
            "sb_skipped_intro_beginning" to "Skipped intro",
            "sb_skipped_intro_middle" to "Skipped intermission",
            "sb_skipped_intro_end" to "Skipped intermission",
            "sb_skipped_outro" to "Skipped outro",
            "sb_skipped_preview_beginning" to "Skipped preview",
            "sb_skipped_preview_middle" to "Skipped preview",
            "sb_skipped_preview_end" to "Skipped recap",
            "sb_skipped_filler" to "Skipped filler",
            "sb_skipped_nomusic" to "Skipped a non-music section",
            "sb_skipped_unsubmitted" to "Skipped unsubmitted segment",
            "sb_skipped_multiple_segments" to "Skipped multiple segments",

            "sb_skip_automatically" to "Skip automatically",
            "sb_skip_automatically_once" to "Skip automatically once",
            "sb_skip_showbutton" to "Show a skip button",
            "sb_skip_seekbaronly" to "Show in seek bar",
            "sb_skip_ignore" to "Disable",

            "sb_submit_failed_invalid" to "Can't submit the segment: %s",
            "sb_submit_failed_timeout" to "Unable to submit segments (API timed out)",
            "sb_submit_failed_unknown_error" to "Unable to submit segments (status: %d %s)",
            "sb_submit_failed_rate_limit" to "Can\'t submit the segment.\nRate Limited (too many from the same user or IP)",
            "sb_submit_failed_forbidden" to "Can\'t submit the segment: %s",
            "sb_submit_failed_duplicate" to "Can\'t submit the segment.\nAlready exists",
            "sb_submit_succeeded" to "Segment submitted successfully",

            "sb_sponsorblock_connection_failure_generic" to "SponsorBlock temporarily not available",
            "sb_sponsorblock_connection_failure_status" to "SponsorBlock temporarily not available (status %d)",
            "sb_sponsorblock_connection_failure_timeout" to "SponsorBlock temporarily not available (API timed out)",

            "sb_vote_failed_timeout" to "Unable to vote for segment (API timed out)",
            "sb_vote_failed_unknown_error" to "Unable to vote for segment (status: %d %s)",
            "sb_vote_failed_forbidden" to "Unable to vote for segment: %s",
            "sb_vote_upvote" to "Upvote",
            "sb_vote_downvote" to "Downvote",
            "sb_vote_category" to "Change category",
            "sb_vote_no_segments" to "There are no segments to vote for",

            "sb_new_segment_choose_category" to "Choose the segment category",
            "sb_new_segment_disabled_category" to "Category is disabled in settings. Enable category to submit.",
            "sb_new_segment_title" to "New SponsorBlock segment",
            "sb_new_segment_mark_time_as_question" to "Set %02d:%02d:%03d as the start or end of a new segment?",
            "sb_new_segment_mark_start" to "start",
            "sb_new_segment_mark_end" to "end",
            "sb_new_segment_now" to "now",
            "sb_new_segment_time_start" to "Time the segment begins at",
            "sb_new_segment_time_end" to "Time the segment ends at",
            "sb_new_segment_confirm_title" to "Are the times correct?",
            "sb_new_segment_confirm_content" to "The segment lasts from %02d:%02d to %02d:%02d (%d minutes %02d seconds)\nIs it ready to submit?",
            "sb_new_segment_start_is_before_end" to "Start must be before the end",
            "sb_new_segment_mark_locations_first" to "Mark two locations on the time bar first",
            "sb_new_segment_preview_segment_first" to "Preview the segment, and ensure it skips smoothly",
            "sb_new_segment_edit_by_hand_title" to "Edit timing of segment manually",
            "sb_new_segment_edit_by_hand_content" to "Do you want to edit the timing for the start or end of the segment?",
            "sb_new_segment_edit_by_hand_parse_error" to "Invalid time given",

            "sb_stats" to "Stats",
            "sb_stats_connection_failure" to "Stats temporarily not available (API is down)",
            "sb_stats_loading" to "Loading...",
            "sb_stats_sb_disabled" to "SponsorBlock is disabled",
            "sb_stats_username" to "Your username: <>b>%s<b>",
            "sb_stats_username_change" to "Tap here to change your username",
            "sb_stats_username_change_unknown_error" to "Unable to change username: Status: %d %s",
            "sb_stats_username_changed" to "Username successfully changed",
            "sb_stats_reputation" to "Your reputation is <b>%.2f</b>",
            "sb_stats_submissions" to "You\'ve created <b>%s</b> segments",
            "sb_stats_saved_zero" to "SponsorBlock leaderboard",
            "sb_stats_saved" to "You\'ve saved people from <b>%s</b> segments",
            "sb_stats_saved_sum_zero" to "Tap here to see the global stats and top contributors",
            "sb_stats_saved_sum" to "That\'s <b>%s</b> of their lives.<br>Tap here to see the leaderboard",
            "sb_stats_self_saved" to "You\'ve skipped <b>%s</b> segments",
            "sb_stats_self_saved_sum" to "That\'s <b>%s</b>",
            "sb_stats_self_saved_reset_title" to "Reset skipped segments counter?",
            "sb_stats_saved_hour_format" to "%d hours %d minutes",
            "sb_stats_saved_minute_format" to "%d minutes %d seconds",
            "sb_stats_saved_second_format" to "%d seconds",

            "sb_color_dot_label" to "Color:",
            "sb_color_changed" to "Color changed",
            "sb_color_reset" to "Color reset",
            "sb_color_invalid" to "Invalid color code",
            "sb_reset_color" to "Reset color",

            "sb_reset" to "Reset",

            "sb_about" to "About",
            "sb_about_api" to "sponsor.ajay.app",
            "sb_about_api_sum" to "Data is provided by the SponsorBlock API. Tap here to learn more and see downloads for other platforms",
            "sb_about_made_by" to "ReVanced integration by JakubWeg,\nrecoded by oSumAtrIX",
        )
    }

    override fun execute(context: ResourceContext) {
        SettingsPatch.addPreference(
            Preference(
                StringResource("revanced_sponsorblock_settings_title", "SponsorBlock"),
                StringResource("revanced_sponsorblock_settings_summary", "SponsorBlock related settings"),
                SettingsPatch.createReVancedSettingsIntent("sponsorblock_settings")
            )
        )
        val classLoader = this.javaClass.classLoader

        /*
         merge SponsorBlock strings to main strings
         */
        context.mergeStrings(strings)

        /*
         merge SponsorBlock drawables to main drawables
         */

        arrayOf(
            ResourceUtils.ResourceGroup(
                "layout",
                "inline_sponsor_overlay.xml",
                "new_segment.xml",
                "skip_sponsor_button.xml"
            ),
            ResourceUtils.ResourceGroup(
                // required resource for back button, because when the base APK is used, this resource will not exist
                "drawable",
                "ic_sb_adjust.xml",
                "ic_sb_compare.xml",
                "ic_sb_edit.xml",
                "ic_sb_logo.xml",
                "ic_sb_publish.xml",
                "ic_sb_voting.xml"
            ),
            ResourceUtils.ResourceGroup(
                // required resource for back button, because when the base APK is used, this resource will not exist
                "drawable-xxxhdpi", "quantum_ic_skip_next_white_24.png"
            )
        ).forEach { resourceGroup ->
            context.copyResources("sponsorblock", resourceGroup)
        }

        /*
        merge xml nodes from the host to their real xml files
         */

        // copy nodes from host resources to their real xml files
        val hostingResourceStream =
            classLoader.getResourceAsStream("sponsorblock/host/layout/youtube_controls_layout.xml")!!

        val targetXmlEditor = context.base.openXmlFile("res/layout/youtube_controls_layout.xml")
        "RelativeLayout".copyXmlNode(
            context.openXmlFile(hostingResourceStream),
            targetXmlEditor
        ).also {
            val children = targetXmlEditor.file.getElementsByTagName("RelativeLayout").item(0).childNodes

            // Replace the startOf with the voting button view so that the button does not overlap
            for (i in 1 until children.length) {
                val view = children.item(i)

                // Replace the attribute for a specific node only
                if (!(view.hasAttributes() && view.attributes.getNamedItem("android:id").nodeValue.endsWith("live_chat_overlay_button"))) continue

                // voting button id from the voting button view from the youtube_controls_layout.xml host file
                val votingButtonId = "@+id/sb_voting_button"

                view.attributes.getNamedItem("android:layout_toStartOf").nodeValue = votingButtonId

                break
            }
        }.close() // close afterwards

    }
}
