package app.revanced.patches.youtube.interaction.downloads.resource.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.impl.ResourceData
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.Dependencies
import app.revanced.patcher.patch.impl.ResourcePatch
import app.revanced.patches.youtube.interaction.downloads.annotation.DownloadsCompatibility
import app.revanced.patches.youtube.misc.manifest.patch.FixLocaleConfigErrorPatch
import app.revanced.patches.youtube.misc.playercontrols.resource.patch.BottomControlsResourcePatch
import app.revanced.util.resources.ResourceUtils
import app.revanced.util.resources.ResourceUtils.copyResources
import app.revanced.util.resources.ResourceUtils.copyXmlNode

@Name("downloads-resource-patch")
@Dependencies([BottomControlsResourcePatch::class, FixLocaleConfigErrorPatch::class])
@Description("Makes necessary changes to resources for the download button.")
@DownloadsCompatibility
@Version("0.0.1")
class DownloadsResourcePatch : ResourcePatch() {
    override fun execute(data: ResourceData): PatchResult {
        /*
         * Copy strings
         */

        data.copyXmlNode("downloads","values/strings.xml", "resources")

        /*
         * Copy resources
         */

        data.copyResources("downloads", ResourceUtils.ResourceGroup("drawable", "revanced_yt_download_button.xml"))

        /*
        * Add download button node
         */

        BottomControlsResourcePatch.addControls("downloads/host/layout/${BottomControlsResourcePatch.TARGET_RESOURCE_NAME}")

        return PatchResultSuccess()
    }
}