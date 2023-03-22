package app.revanced.patches.twitter.misc.dynamiccolor.patch

import app.revanced.patcher.ResourceContext
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.twitter.misc.dynamiccolor.annotations.DynamicColorCompatibility
import java.io.FileWriter
import java.nio.file.Files

@Patch
@Name("dynamic-color")
@Description("Replaces the default Twitter Blue with the users Material You palette.")
@DynamicColorCompatibility
@Version("0.0.1")
class DynamicColorPatch : ResourcePatch {
    override fun execute(context: ResourceContext): PatchResult {
        listOf("values-v31", "values-night-v31").forEach {
            val colorsXml = "res/$it/colors.xml"
            context.apkBundle.base.openFile(colorsXml).use { file ->
                if (!file.exists) {
                    file.writeText("<?xml version=\"1.0\" encoding=\"utf-8\"?><resources></resources>")
                }
            }
        }

        context.openEditor("res/values-v31/colors.xml").use { editor ->
            val document = editor.file

            mapOf(
                "ps__twitter_blue" to "@color/twitter_blue",
                "ps__twitter_blue_pressed" to "@color/twitter_blue_fill_pressed",
                "twitter_blue" to "@android:color/system_accent1_400",
                "twitter_blue_fill_pressed" to "@android:color/system_accent1_300",
                "twitter_blue_opacity_30" to "@android:color/system_accent1_100",
                "twitter_blue_opacity_50" to "@android:color/system_accent1_200",
                "twitter_blue_opacity_58" to "@android:color/system_accent1_300",
                "deep_transparent_twitter_blue" to "@android:color/system_accent1_200",
                "ic_launcher_background" to "#1DA1F2"
            ).forEach { (k, v) ->
                val colorElement = document.createElement("color")

                colorElement.setAttribute("name", k)
                colorElement.textContent = v

                document.getElementsByTagName("resources").item(0).appendChild(colorElement)
            }
        }

        context.openEditor("res/values-night-v31/colors.xml").use { editor ->
            val document = editor.file

            mapOf(
                "twitter_blue" to "@android:color/system_accent1_200",
                "twitter_blue_fill_pressed" to "@android:color/system_accent1_300",
                "twitter_blue_opacity_30" to "@android:color/system_accent1_50",
                "twitter_blue_opacity_50" to "@android:color/system_accent1_100",
                "twitter_blue_opacity_58" to "@android:color/system_accent1_200",
                "deep_transparent_twitter_blue" to "@android:color/system_accent1_200"
            ).forEach { (k, v) ->
                val colorElement = document.createElement("color")

                colorElement.setAttribute("name", k)
                colorElement.textContent = v

                document.getElementsByTagName("resources").item(0).appendChild(colorElement)
            }
        }

        return PatchResult.Success
    }
}
