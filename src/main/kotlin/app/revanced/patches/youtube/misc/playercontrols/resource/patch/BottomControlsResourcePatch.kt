package app.revanced.patches.youtube.misc.playercontrols.resource.patch

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.impl.ResourceData
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.impl.ResourcePatch
import app.revanced.patches.youtube.interaction.overlaybuttons.annotation.OverlayButtonsCompatibility
import app.revanced.patches.youtube.misc.manifest.patch.FixLocaleConfigErrorPatch
import app.revanced.util.resources.ResourceUtils.copyXmlNode
import java.nio.file.Files

@Name("bottom-controls-resource-patch")
@OverlayButtonsCompatibility
@DependsOn([FixLocaleConfigErrorPatch::class])
@Version("0.0.1")
class BottomControlsResourcePatch : ResourcePatch() {
    override fun execute(data: ResourceData): PatchResult {
        /*
         * Copy preference fragments
         */

        data.copyXmlNode("overlaybuttons/host", "layout/youtube_controls_bottom_ui_container.xml", "android.support.constraint.ConstraintLayout")

        val container = data["res/layout/youtube_controls_bottom_ui_container.xml"]
        container.writeText(
            container.readText()
			.replace(
                "yt:layout_constraintRight_toLeftOf=\"@id/fullscreen_button",
                "yt:layout_constraintRight_toLeftOf=\"@+id/copy_button"
            ).replace(
                "android.support.constraint.ConstraintLayout",
                "androidx.constraintlayout.widget.ConstraintLayout"
            )
        )

        return PatchResultSuccess()
    }
}
