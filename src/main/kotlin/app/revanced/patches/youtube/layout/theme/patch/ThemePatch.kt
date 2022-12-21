package app.revanced.patches.youtube.layout.theme.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.*
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.youtube.layout.theme.annotations.ThemeCompatibility
import app.revanced.patches.youtube.misc.manifest.patch.FixLocaleConfigErrorPatch
import org.w3c.dom.Element

@Patch(include = false)
@DependsOn([LithoThemePatch::class, FixLocaleConfigErrorPatch::class])
@Name("theme")
@Description("Applies a custom theme.")
@ThemeCompatibility
@Version("0.0.1")
class ThemePatch : ResourcePatch {
    override fun execute(context: ResourceContext): PatchResult {
        val darkThemeBackgroundColor = darkThemeBackgroundColor!!
        val lightThemeBackgroundColor = lightThemeBackgroundColor!!

        context.xmlEditor["res/values/colors.xml"].use { editor ->
            val resourcesNode = editor.file.getElementsByTagName("resources").item(0) as Element

            for (i in 0 until resourcesNode.childNodes.length) {
                val node = resourcesNode.childNodes.item(i) as? Element ?: continue

                node.textContent = when (node.getAttribute("name")) {
                    "yt_black0", "yt_black1", "yt_black1_opacity95", "yt_black1_opacity98", "yt_black2", "yt_black3",
                    "yt_black4", "yt_status_bar_background_dark", "material_grey_100", "material_grey_50",
                    "material_grey_600", "material_grey_800", "material_grey_850", "material_grey_900",
                    "material_grey_white_1000", "sud_glif_v3_dialog_background_color_dark" -> darkThemeBackgroundColor

                    "yt_white1", "yt_white1_opacity95", "yt_white1_opacity98", "yt_white2", "yt_white3", "yt_white4",
                    "sud_glif_v3_dialog_background_color_light" -> lightThemeBackgroundColor

                    else -> continue
                }
            }
        }

        // copies the resource file to change the splash screen color
        context.xmlEditor["res/values/attrs.xml"].use { editor ->
            with(editor.file) {
                val resourcesNode = getElementsByTagName("resources").item(0) as Element

                val newElement: Element = createElement("attr")
                newElement.setAttribute("format", "reference")
                newElement.setAttribute("name", "splashScreenColor")

                resourcesNode.appendChild(newElement)
            }
        }
        context.xmlEditor["res/values/styles.xml"].use { editor ->
            with(editor.file) {
                val resourcesNode = getElementsByTagName("resources").item(0) as Element

                for (i in 0 until resourcesNode.childNodes.length) {
                    val node = resourcesNode.childNodes.item(i) as? Element ?: continue

                    val newElement: Element = createElement("item")
                    newElement.setAttribute("name", "splashScreenColor")

                    when (node.getAttribute("name")) {
                        "Base.Theme.YouTube.Launcher.Dark" -> {
                            newElement.appendChild(createTextNode(darkThemeBackgroundColor))

                            node.appendChild(newElement)
                        }
                        "Base.Theme.YouTube.Launcher.Light" -> {
                            newElement.appendChild(createTextNode(lightThemeBackgroundColor));

                            node.appendChild(newElement)
                        }
                    }
                }
            }
        }
        context.xmlEditor["res/values-v31/styles.xml"].use { editor ->
            with(editor.file) {
                val resourcesNode = getElementsByTagName("resources").item(0) as Element

                val newElement: Element = createElement("item")
                newElement.setAttribute("name", "android:windowSplashScreenBackground")

                for (i in 0 until resourcesNode.childNodes.length) {
                    val node = resourcesNode.childNodes.item(i) as? Element ?: continue

                    if (node.getAttribute("name") == "Base.Theme.YouTube.Launcher") {
                        newElement.appendChild(createTextNode("?attr/splashScreenColor"))

                        node.appendChild(newElement)
                    }
                }
            }
        }
        arrayOf("drawable", "drawable-sw600dp").forEach { drawablePath ->
        context.xmlEditor["res/$drawablePath/quantum_launchscreen_youtube.xml"].use { editor ->
            val resourcesNode = editor.file.getElementsByTagName("item").item(0) as Element

            if (resourcesNode.attributes.getNamedItem("android:drawable") != null)
                resourcesNode.setAttribute("android:drawable", "?attr/splashScreenColor")
        }

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

        var lightThemeBackgroundColor: String? by option(
            PatchOption.StringOption(
                key = "lightThemeBackgroundColor",
                default = "@android:color/white",
                title = "Background color for the light theme",
                description = "The background color of the light theme. Can be a hex color or a resource reference.",
            )
        )
    }
}
