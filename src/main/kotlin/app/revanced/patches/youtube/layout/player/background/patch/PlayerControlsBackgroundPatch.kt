package app.revanced.patches.youtube.layout.player.background.patch

import app.revanced.extensions.doRecursively
import app.revanced.patcher.ResourceContext
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.youtube.layout.player.background.annotations.PlayerControlsBackgroundCompatibility
import app.revanced.util.resources.ResourceUtils.base
import org.w3c.dom.Element

@Patch(false)
@Name("remove-player-controls-background")
@Description("Removes the background from the video player controls.")
@PlayerControlsBackgroundCompatibility
@Version("0.0.1")
class PlayerControlsBackgroundPatch : ResourcePatch {
    override fun execute(context: ResourceContext) {
        context.base.openXmlFile(RESOURCE_FILE_PATH).use { editor ->
            editor.file.doRecursively node@{ node ->
                if (node !is Element) return@node

                node.getAttributeNode("android:color")?.let { attribute ->
                    attribute.textContent = "@android:color/transparent"
                }
            }
        }

    }

    private companion object {
        const val RESOURCE_FILE_PATH = "res/drawable/player_button_circle_background.xml"
    }
}
