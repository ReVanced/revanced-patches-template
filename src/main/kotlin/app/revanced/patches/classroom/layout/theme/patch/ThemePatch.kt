package app.revanced.patches.classroom.layout.theme.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.*
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.classroom.layout.theme.annotations.ThemeCompatibility
import app.revanced.util.resources.ResourceUtils
import app.revanced.util.resources.ResourceUtils.copyResources
import org.w3c.dom.Element

@Patch()
@DependsOn()
@Name("amoled-theme")
@Description("Applies amoled theme.")
@ThemeCompatibility
@Version("0.0.1")
class ThemePatch : ResourcePatch {
    override fun execute(context: ResourceContext): PatchResult {
        val darkThemeBackgroundColor = darkThemeBackgroundColor!!
        // val lightThemeBackgroundColor = lightThemeBackgroundColor!!

        context.xmlEditor["res/values/colors.xml"].use { editor ->
            val resourcesNode = editor.file.getElementsByTagName("resources").item(0) as Element

            for (i in 0 until resourcesNode.childNodes.length) {
                val node = resourcesNode.childNodes.item(i) as? Element ?: continue

                node.textContent = when (node.getAttribute("name")) {
                    "default_background", "design_dark_default_color_on_background", "design_dark_default_color_on_surface", "design_default_color_background", "design_default_color_surface", "foreground_material_dark",
                    "foreground_material_light", "primary_material_light" -> darkThemeBackgroundColor

                    // "yt_white1", "yt_white1_opacity95", "yt_white1_opacity98", "yt_white2", "yt_white3", "yt_white4",
                    // "sud_glif_v3_dialog_background_color_light" -> lightThemeBackgroundColor

                    //  "material_grey_100", "material_grey_50",
                    // "material_grey_600", "material_grey_800", "material_grey_850", "material_grey_900",
                    // "material_grey_white_1000", "sud_glif_v3_dialog_background_color_dark"

                    else -> continue
                }
            }
        }

        // copies the resource file to change the splash screen color
        context.copyResources("theme",
            ResourceUtils.ResourceGroup("values-night-v31", "styles.xml")
        )

        return PatchResultSuccess()
    }

    companion object : OptionsContainer() {
        var darkThemeBackgroundColor: String? by option(
            PatchOption.StringOption(
                key = "darkThemeBackgroundColor",
                default = "@android:color/black",
                title = "Background color for the dark theme",
                description = "The background color of the dark theme. Can be a hex color or a resource reference.",
            )
        )

        // var lightThemeBackgroundColor: String? by option(
        //     PatchOption.StringOption(
        //         key = "lightThemeBackgroundColor",
        //         default = "@android:color/white",
        //         title = "Background color for the light theme",
        //         description = "The background color of the light theme. Can be a hex color or a resource reference.",
        //     )
        // )
    }
}
