package app.revanced.patches.youtube.layout.hide.player.overlay.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.ResourceContext
import app.revanced.patcher.patch.PatchException
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.youtube.layout.hide.player.overlay.annotations.HidePlayerOverlayPatchCompatibility
import app.revanced.util.resources.ResourceUtils.base

@Patch(false)
@Name("hide-player-overlay")
@Description("Hides the dark player overlay when player controls are visible.")
@HidePlayerOverlayPatchCompatibility
@Version("0.0.1")
class HidePlayerOverlayPatch : ResourcePatch {
    override fun execute(context: ResourceContext): PatchResult {
        val attributes = arrayOf("height", "width")

        context.base.editXmlFile(RESOURCE_FILE_PATH).use { editor ->
            editor.file.getElementsByTagName("FrameLayout").item(0).childNodes.apply {
                for (i in 1 until length) {
                    val view = item(i)
                    if (
                        view.attributes.getNamedItem("android:id")
                            ?.nodeValue
                            ?.endsWith("scrim_overlay") == true
                    ) {
                        attributes.forEach {
                            view.attributes.getNamedItem("android:layout_$it").nodeValue = "0.0dip"
                        }
                        break
                    }
                }
            }
        }

        return PatchResult.Success
    }

    private companion object {
        const val RESOURCE_FILE_PATH = "res/layout/youtube_controls_overlay.xml"
    }
}
