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
            val childNodes = (editor.file.getElementsByTagName("resources").item(0) as Element).childNodes

            for (i in 0 until childNodes.length) {
                val node = childNodes.item(i) as? Element ?: continue

                node.textContent = when (node.getAttribute("name")) {
                    "yt_black0", "yt_black1", "yt_black1_opacity95", "yt_black1_opacity98", "yt_black2", "yt_black3",
                    "yt_black4", "yt_status_bar_background_dark", "material_grey_850" -> darkThemeBackgroundColor

                    "yt_white1", "yt_white1_opacity95", "yt_white1_opacity98", "yt_white2", "yt_white3", "yt_white4"
                    -> lightThemeBackgroundColor

                    else -> continue
                }
            }
        }

        // edit the resource files to change the splash screen color
        val attrsPath = "res/values/attrs.xml";
        val stylesPaths: List<String> = listOf(
            "res/values/styles.xml", // Android 11 (and below)
            "res/values-v31/styles.xml", // Android 12 (and above)
        )
        val quantumLaunchscreenPaths: List<String> = listOf(
            "res/drawable/quantum_launchscreen_youtube.xml",
            "res/drawable-anydpi/quantum_launchscreen_youtube.xml",
            "res/drawable-hdpi/quantum_launchscreen_youtube.xml",
            "res/drawable-ldrtl/quantum_launchscreen_youtube.xml",
            "res/drawable-ldrtl-night/quantum_launchscreen_youtube.xml",
            "res/drawable-ldrtl-xxhdpi/quantum_launchscreen_youtube.xml",
            "res/drawable-mdpi/quantum_launchscreen_youtube.xml",
            "res/drawable-night/quantum_launchscreen_youtube.xml",
            "res/drawable-nodpi/quantum_launchscreen_youtube.xml",
            "res/drawable-sw600dp/quantum_launchscreen_youtube.xml",
            "res/drawable-sw600dp-xhdpi/quantum_launchscreen_youtube.xml",
            "res/drawable-sw720dp-xhdpi/quantum_launchscreen_youtube.xml",
            "res/drawable-watch/quantum_launchscreen_youtube.xml",
            "res/drawable-xhdpi/quantum_launchscreen_youtube.xml",
            "res/drawable-xxhdpi/quantum_launchscreen_youtube.xml",
            "res/drawable-xxxhdpi/quantum_launchscreen_youtube.xml"
        )

        context.xmlEditor[attrsPath].use { editor ->
            val file = editor.file

            (file.getElementsByTagName("resources").item(0) as Element).appendChild(
                file.createElement("attr").apply {
                    setAttribute("format", "reference")
                    setAttribute("name", "splashScreenColor")
                }
            )
        }
        stylesPaths.forEachIndexed { pathIndex, stylesPath ->
            context.xmlEditor[stylesPath].use { editor ->
                val file = editor.file

                val childNodes = (file.getElementsByTagName("resources").item(0) as Element).childNodes

                for (i in 0 until childNodes.length) {
                    val node = childNodes.item(i) as? Element ?: continue
                    val nodeAttributeName = node.getAttribute("name")

                    file.createElement("item").apply {
                        setAttribute(
                            "name",
                            when (pathIndex) {
                                0 -> "splashScreenColor"
                                1 -> "android:windowSplashScreenBackground"
                                else -> "null"
                            }
                        )

                        appendChild(
                            file.createTextNode(
                                when (pathIndex) {
                                    0 -> when (nodeAttributeName) {
                                        "Base.Theme.YouTube.Launcher.Dark" -> darkThemeBackgroundColor
                                        "Base.Theme.YouTube.Launcher.Light" -> lightThemeBackgroundColor
                                        else -> "null"
                                    }
                                    1 -> when (nodeAttributeName) {
                                        "Base.Theme.YouTube.Launcher" -> "?attr/splashScreenColor"
                                        else -> "null"
                                    }
                                    else -> "null"
                                }


                            )
                        )

                        if (this.textContent != "null")
                            node.appendChild(this)
                    }
                }
            }
        }
        quantumLaunchscreenPaths.forEach { quantumLaunchscreenPath ->
            if (context[quantumLaunchscreenPath].exists()) {
                context.xmlEditor[quantumLaunchscreenPath].use { editor ->
                    val resourcesNode = editor.file.getElementsByTagName("item").item(0) as Element

                    if (resourcesNode.attributes.getNamedItem("android:drawable") != null)
                        resourcesNode.setAttribute("android:drawable", "?attr/splashScreenColor")
                }
            }
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
