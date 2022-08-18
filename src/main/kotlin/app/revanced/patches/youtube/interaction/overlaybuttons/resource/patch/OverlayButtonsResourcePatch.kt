package app.revanced.patches.youtube.interaction.overlaybuttons.resource.patch

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.impl.ResourceData
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.impl.ResourcePatch
import app.revanced.patches.youtube.interaction.overlaybuttons.annotation.OverlayButtonsCompatibility
import app.revanced.patches.youtube.misc.manifest.patch.FixLocaleConfigErrorPatch
import app.revanced.patches.youtube.misc.playercontrols.resource.patch.BottomControlsResourcePatch
import app.revanced.util.resources.ResourceUtils
import app.revanced.util.resources.ResourceUtils.copyResources
import app.revanced.util.resources.ResourceUtils.copyXmlNode

@Name("overlay-buttons-resource-patch")
@OverlayButtonsCompatibility
@DependsOn([BottomControlsResourcePatch::class, FixLocaleConfigErrorPatch::class])
@Version("0.0.1")
class OverlayButtonsResourcePatch : ResourcePatch() {
    override fun execute(data: ResourceData): PatchResult {

        /*
         * Copy resources
         */

        data.copyResources(
            "overlaybuttons",
            ResourceUtils.ResourceGroup(
                "drawable",

                "revanced_yt_copy_icon.xml",
                "revanced_yt_copy_icon_with_time.xml",
                "revanced_yt_repeat_icon.xml"
            )
        )

        return PatchResultSuccess()
    }
}