package app.revanced.patches.youtube.interaction.downloads.resource.patch

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patches.shared.settings.preference.impl.*
import app.revanced.patches.youtube.misc.playercontrols.resource.patch.BottomControlsResourcePatch
import app.revanced.patches.youtube.misc.settings.bytecode.patch.YouTubeSettingsPatch
import app.revanced.util.resources.ResourceUtils
import app.revanced.util.resources.ResourceUtils.copyResources

@DependsOn([BottomControlsResourcePatch::class, YouTubeSettingsPatch::class])
class ExternalDownloadsResourcePatch : ResourcePatch {

    override fun execute(context: ResourceContext): PatchResult {
        YouTubeSettingsPatch.PreferenceScreen.INTERACTIONS.addPreferences(
            PreferenceScreen(
                "revanced_external_downloader_preference_screen",
                "revanced_external_downloader_preference_screen_title",
                listOf(
                    SwitchPreference(
                        "revanced_external_downloader",
                        "revanced_external_downloader_title",
                        "revanced_external_downloader_summary_on",
                        "revanced_external_downloader_summary_off"
                    ),
                    TextPreference(
                        "revanced_external_downloader_name",
                        "revanced_external_downloader_name_title",
                        "revanced_external_downloader_name_summary",
                        InputType.TEXT
                    )
                ),
                "revanced_external_downloader_preference_screen_summary"
            )
        )

        // Copy resources
        context.copyResources(
            "youtube/downloads",
            ResourceUtils.ResourceGroup("drawable", "revanced_ic_download_button.xml")
        )

        // Add download button node
        BottomControlsResourcePatch.addControls("youtube/downloads/host/layout/${BottomControlsResourcePatch.TARGET_RESOURCE_NAME}")

        return PatchResultSuccess()
    }
}