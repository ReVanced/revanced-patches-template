package app.revanced.patches.youtube.layout.seekbar.resource

import app.revanced.patcher.ResourceContext
import app.revanced.patcher.patch.*
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch
import app.revanced.util.resources.ResourceUtils.base
import app.revanced.util.resources.ResourceUtils.resourceIdOf
import org.w3c.dom.Element

@DependsOn([SettingsPatch::class])
class SeekbarColorResourcePatch : ResourcePatch {
    override suspend fun execute(context: ResourceContext) {
        // Edit theme colors via bytecode.
        // For that the resource id is used in a bytecode patch to change the color.
        inlineTimeBarColorizedBarPlayedColorDarkId = context.resourceIdOf("color","inline_time_bar_colorized_bar_played_color_dark")
        inlineTimeBarPlayedNotHighlightedColorId = context.resourceIdOf("color","inline_time_bar_played_not_highlighted_color")

        // Edit the resume playback drawable and replace the progress bar with a custom drawable
        context.base.openXmlFile("res/drawable/resume_playback_progressbar_drawable.xml").use { editor ->
            val layerList = editor.file.getElementsByTagName("layer-list").item(0) as Element
            val progressNode = layerList.getElementsByTagName("item").item(1) as Element
            if (!progressNode.getAttributeNode("android:id").value.endsWith("progress")) {
                throw PatchException("Could not find progress bar")
            }
            val scaleNode = progressNode.getElementsByTagName("scale").item(0) as Element
            val shapeNode = scaleNode.getElementsByTagName("shape").item(0) as Element
            val replacementNode = editor.file.createElement(
                "app.revanced.integrations.patches.theme.ProgressBarDrawable")
            scaleNode.replaceChild(replacementNode, shapeNode)
        }
    }

    companion object {
        internal var inlineTimeBarColorizedBarPlayedColorDarkId = -1L
        internal var inlineTimeBarPlayedNotHighlightedColorId = -1L
    }
}
