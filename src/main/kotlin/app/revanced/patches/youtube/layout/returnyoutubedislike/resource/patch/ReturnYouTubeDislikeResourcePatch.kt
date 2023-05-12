package app.revanced.patches.youtube.layout.returnyoutubedislike.resource.patch

import app.revanced.patcher.ResourceContext
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patches.shared.mapping.misc.patch.ResourceMappingPatch
import app.revanced.patches.shared.settings.preference.impl.Preference
import app.revanced.patches.shared.settings.preference.impl.StringResource
import app.revanced.patches.youtube.layout.returnyoutubedislike.annotations.ReturnYouTubeDislikeCompatibility
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch
import app.revanced.util.resources.ResourceUtils.mergeStrings

@DependsOn([SettingsPatch::class])
@Name("return-youtube-dislike-resource-patch")
@Description("Adds the preferences for Return YouTube Dislike.")
@ReturnYouTubeDislikeCompatibility
@Version("0.0.1")
class ReturnYouTubeDislikeResourcePatch : ResourcePatch {
    companion object {
        internal var oldUIDislikeId: Long = -1

        private val strings = mapOf(
            "revanced_ryd_video_likes_hidden_by_video_owner" to "Hidden",

            "revanced_ryd_failure_connection_timeout" to "Dislikes temporarily not available (API timed out)",
            "revanced_ryd_failure_connection_status_code" to "Dislikes not available (status %d)",
            "revanced_ryd_failure_client_rate_limit_requested" to "Dislikes not available (client API limit reached)",
            "revanced_ryd_failure_generic" to "Dislikes not available (%s)",

            // corner case situation, where user enables RYD while video is playing and then tries to vote for the video
            "revanced_ryd_failure_ryd_enabled_while_playing_video_then_user_voted" to "Reload video to vote using ReturnYouTubeDislike",

            "revanced_ryd_enable_title" to "Return YouTube Dislike",
            "revanced_ryd_enable_summary_on" to "Dislikes are shown",
            "revanced_ryd_enable_summary_off" to "Dislikes are not shown",

            "revanced_ryd_dislike_percentage_title" to "Dislikes as percentage",
            "revanced_ryd_dislike_percentage_summary_on" to "Dislikes shown as percentage",
            "revanced_ryd_dislike_percentage_summary_off" to "Dislikes shown as number",

            "revanced_ryd_compact_layout_title" to "Compact like button",
            "revanced_ryd_compact_layout_summary_on" to "Like button styled for minimum width",
            "revanced_ryd_compact_layout_summary_off" to "Like button styled for best appearance",

            "revanced_ryd_about" to "About",
            "revanced_ryd_attribution_title" to "ReturnYouTubeDislike.com",
            "revanced_ryd_attribution_summary" to "Dislike data is provided by the Return YouTube Dislike API.  Tap here to learn more.",


            "revanced_ryd_statistics_category_title" to "ReturnYouTubeDislike API statistics of this device",

            "revanced_ryd_statistics_getFetchCallResponseTimeAverage_title" to "API response time, average",
            "revanced_ryd_statistics_getFetchCallResponseTimeMin_title" to "API response time, minimum",
            "revanced_ryd_statistics_getFetchCallResponseTimeMax_title" to "API response time, maximum",

            "revanced_ryd_statistics_getFetchCallResponseTimeLast_title" to "API response time, last video",
            "revanced_ryd_statistics_getFetchCallResponseTimeLast_rate_limit_summary" to "Dislikes temporarily not available - Client API rate limit in effect",

            "revanced_ryd_statistics_getFetchCallCount_title" to "API fetch votes, number of calls",
            "revanced_ryd_statistics_getFetchCallCount_zero_summary" to "No network calls made",
            "revanced_ryd_statistics_getFetchCallCount_non_zero_summary" to "%d network calls made",

            "revanced_ryd_statistics_getFetchCallNumberOfFailures_title" to "API fetch votes, number of timeouts",
            "revanced_ryd_statistics_getFetchCallNumberOfFailures_zero_summary" to "No network calls timed out",
            "revanced_ryd_statistics_getFetchCallNumberOfFailures_non_zero_summary" to "%d network calls timed out",

            "revanced_ryd_statistics_getNumberOfRateLimitRequestsEncountered_title" to "API client rate limits",
            "revanced_ryd_statistics_getNumberOfRateLimitRequestsEncountered_zero_summary" to "No client rate limits encountered",
            "revanced_ryd_statistics_getNumberOfRateLimitRequestsEncountered_non_zero_summary" to "Client rate limit encountered %d times",

            "revanced_ryd_statistics_millisecond_text" to "%d milliseconds",

            )
    }

    override fun execute(context: ResourceContext) {
        val youtubePackage = "com.google.android.youtube"
        SettingsPatch.addPreference(
            Preference(
                StringResource("revanced_ryd_settings_title", "Return YouTube Dislike"),
                Preference.Intent(
                    youtubePackage,
                    "ryd_settings",
                    "com.google.android.libraries.social.licenses.LicenseActivity"
                ),
                StringResource("revanced_ryd_settings_summary", "Settings for Return YouTube Dislike"),
            )
        )
        // merge strings
        context.mergeStrings(strings)

        oldUIDislikeId = ResourceMappingPatch.resourceIdOf("id", "dislike_button")
    }
}