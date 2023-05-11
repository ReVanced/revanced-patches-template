package app.revanced.patches.youtube.interaction.downloads.resource.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patches.shared.settings.preference.impl.*
import app.revanced.patches.youtube.interaction.downloads.annotation.DownloadsCompatibility
import app.revanced.patches.youtube.misc.playercontrols.resource.patch.BottomControlsResourcePatch
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch
import app.revanced.util.resources.ResourceUtils
import app.revanced.util.resources.ResourceUtils.copyResources
import app.revanced.util.resources.ResourceUtils.mergeStrings

@Name("downloads-resource-patch")
@DependsOn([BottomControlsResourcePatch::class, SettingsPatch::class])
@Description("Makes necessary changes to resources for the download button.")
@DownloadsCompatibility
@Version("0.0.1")
class DownloadsResourcePatch : ResourcePatch {
    override fun execute(context: ResourceContext): PatchResult {
        SettingsPatch.PreferenceScreen.INTERACTIONS.addPreferences(
            PreferenceScreen(
                "revanced_external_downloader_preference_screen",
                StringResource("revanced_external_downloader_preference_screen_title", "Download settings"),
                listOf(
                    SwitchPreference(
                        "revanced_external_downloader",
                        StringResource("revanced_external_downloader_title", "Show download button"),
                        StringResource("revanced_external_downloader_summary_on", "Download button is shown"),
                        StringResource("revanced_external_downloader_summary_off", "Download button is not shown")
                    ),
                    TextPreference(
                        "revanced_external_downloader_name",
                        StringResource("revanced_external_downloader_name_title", "Downloader package name"),
                        InputType.TEXT,
                        StringResource("revanced_external_downloader_name_summary", "Package name of the downloader app such as NewPipe\\'s or PowerTube\\'s")
                    )
                ),
                StringResource("revanced_external_downloader_preference_screen_summary", "Settings related to downloads")
            )
        )


        /*
         * Copy strings
         */

        context.mergeStrings("downloads/host/values/strings.xml")

        /*
         * Copy resources
         */

        context.copyResources("downloads", ResourceUtils.ResourceGroup("drawable", "revanced_yt_download_button.xml"))

        /*
        * Add download button node
         */

        BottomControlsResourcePatch.addControls("downloads/host/layout/${BottomControlsResourcePatch.TARGET_RESOURCE_NAME}")

        return PatchResultSuccess()
    }
}