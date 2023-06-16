package app.revanced.patches.youtube.interaction.copyvideourl.resource.patch

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patches.shared.settings.preference.impl.PreferenceScreen
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.youtube.misc.playercontrols.resource.patch.BottomControlsResourcePatch
import app.revanced.patches.youtube.misc.settings.bytecode.patch.YouTubeSettingsPatch
import app.revanced.util.resources.ResourceUtils
import app.revanced.util.resources.ResourceUtils.copyResources

@Name("copy-video-url-resource")
@DependsOn([BottomControlsResourcePatch::class, YouTubeSettingsPatch::class])
@Version("0.0.1")
class CopyVideoUrlResourcePatch : ResourcePatch {
    override fun execute(context: ResourceContext): PatchResult {
        YouTubeSettingsPatch.PreferenceScreen.INTERACTIONS.addPreferences(
            PreferenceScreen(
                "revanced_copy_video_url_preference_screen",
                "revanced_copy_video_url_preference_screen_title",
                listOf(
                    SwitchPreference(
                        "revanced_copy_video_url",
                        "revanced_copy_video_url_title",
                        "revanced_copy_video_url_summary_on",
                        "revanced_copy_video_url_summary_off"
                    ),
                    SwitchPreference(
                        "revanced_copy_video_url_timestamp",
                        "revanced_copy_video_url_timestamp_title",
                        "revanced_copy_video_url_timestamp_summary_on",
                        "revanced_copy_video_url_timestamp_summary_off"
                    )
                ),
                "revanced_copy_video_url_preference_screen_summary"
            )
        )

        context.copyResources(
            "youtube/copyvideourl",
            ResourceUtils.ResourceGroup(
                resourceDirectoryName = "drawable",
                "revanced_ic_copy_video_url.xml",
                "revanced_ic_copy_video_timestamp.xml"
            )
        )

        BottomControlsResourcePatch.addControls("youtube/copyvideourl/host/layout/${BottomControlsResourcePatch.TARGET_RESOURCE_NAME}")

        return PatchResultSuccess()
    }
}