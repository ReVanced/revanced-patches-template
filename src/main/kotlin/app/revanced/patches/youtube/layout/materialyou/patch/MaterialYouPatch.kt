package app.revanced.patches.youtube.layout.materialyou.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.implementation.ResourceData
import app.revanced.patcher.patch.annotations.Dependencies
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.patch.implementation.ResourcePatch
import app.revanced.patcher.patch.implementation.misc.PatchResult
import app.revanced.patcher.patch.implementation.misc.PatchResultSuccess
import app.revanced.patches.youtube.layout.materialyou.annotations.MaterialYouCompatibility
import app.revanced.patches.youtube.misc.manifest.patch.FixLocaleConfigErrorPatch
import org.w3c.dom.Element
import java.io.File

@Patch
@Dependencies(
    dependencies = [
        FixLocaleConfigErrorPatch::class
    ]
)
@Name("materialyou")
@Description("Enables Material Background Colors.")
@MaterialYouCompatibility
@Version("0.0.1")
class MaterialYouPatch : ResourcePatch() {
    override fun execute(data: ResourceData): PatchResult {
        data.getXmlEditor("res${File.separator}values${File.separator}colors.xml").use { editor ->
            val resourcesNode = editor.file.getElementsByTagName("resources").item(0) as Element

            for (i in 0 until resourcesNode.childNodes.length) {
                val node = resourcesNode.childNodes.item(i)
                if (node !is Element) continue

                val element = resourcesNode.childNodes.item(i) as Element
                element.textContent = when (element.getAttribute("name")) {
                    "yt_black1", "yt_black1_opacity95", "yt_black2", "yt_black3", "yt_black4", "yt_status_bar_background_dark" -> "@android:color/system_neutral1_900"
                    "yt_white1", "yt_white1_opacity95", "yt_white2", "yt_white3", "yt_white4", "yt_status_bar_background_light" -> "@android:color/system_neutral1_10"
                    "yt_selected_nav_label_dark", "yt_selected_nav_label_light" -> "#ffdf0000"
                    else -> continue
                }
            }
        }

        return PatchResultSuccess()
    }
}
