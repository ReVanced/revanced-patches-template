package app.revanced.patches.spotify.layout.theme

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patcher.patch.options.PatchOption.PatchExtensions.stringPatchOption
import org.w3c.dom.Element

@Patch(
    name = "Custom theme",
    description = "Applies a custom theme.",
    compatiblePackages = [CompatiblePackage("com.spotify.music")]
)
@Suppress("unused")
object CustomThemePatch : ResourcePatch() {
    private var backgroundColor by stringPatchOption(
        key = "backgroundColor",
        default = "@android:color/black",
        title = "Primary Background color",
        description = "The background color. Can be a hex color or a resource reference.",
    )

    private var backgroundColorSecondary by stringPatchOption(
        key = "backgroundColorSecondary",
        default = "#ff282828",
        title = "Secondary background color",
        description = "The secondary background color. Can be a hex color or a resource reference.",
    )

    private var accentColor by stringPatchOption(
        key = "accentColor",
        default = "#ff1ed760",
        title = "Accent color",
        description = "The accent color ('Spotify green' by default). Can be a hex color or a resource reference.",
    )

    private var accentColorPressed by stringPatchOption(
        key = "accentColorPressed",
        default = "#ff169c46",
        title = "Pressed dark theme accent color",
        description = "The color when accented buttons are pressed, by default slightly darker than accent. "
                + "Can be a hex color or a resource reference."
    )

    override fun execute(context: ResourceContext) {
        val backgroundColor = backgroundColor!!
        val backgroundColorSecondary = backgroundColorSecondary!!
        val accentColor = accentColor!!
        val accentColorPressed = accentColorPressed!!

        context.xmlEditor["res/values/colors.xml"].use { editor ->
            val resourcesNode = editor.file.getElementsByTagName("resources").item(0) as Element

            for (i in 0 until resourcesNode.childNodes.length) {
                val node = resourcesNode.childNodes.item(i) as? Element ?: continue

                node.textContent = when (node.getAttribute("name")) {
                    "dark_base_background_elevated_base", "design_dark_default_color_background",
                    "design_dark_default_color_surface", "gray_7", "gray_background", "gray_layer",
                    "sthlm_blk" -> backgroundColor

                    "gray_15" -> backgroundColorSecondary

                    "gray_15" -> backgroundSecondaryColor!!
                    "dark_brightaccent_background_base", "dark_base_text_brightaccent", "green_light" -> accentColor

                    "dark_brightaccent_background_press" -> accentColorPressed
                    else -> continue
                }
            }
        }
    }
}
