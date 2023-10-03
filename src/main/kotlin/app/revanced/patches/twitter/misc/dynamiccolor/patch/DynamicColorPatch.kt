package app.revanced.patches.twitter.misc.dynamiccolor.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.PatchException
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.twitter.misc.dynamiccolor.annotations.DynamicColorCompatibility
import org.w3c.dom.Node
import java.io.FileWriter
import java.nio.file.Files

@Patch
@Name("Dynamic color")
@Description("Replaces the default Twitter Blue with the users Material You palette.")
@DynamicColorCompatibility
class DynamicColorPatch : ResourcePatch {
    override fun execute(context: ResourceContext) {
        val resDirectory = context["res"]
        if (!resDirectory.isDirectory) throw PatchException("The res folder can not be found.")

        val valuesV31Directory = resDirectory.resolve("values-v31")
        if (!valuesV31Directory.isDirectory) Files.createDirectories(valuesV31Directory.toPath())

        val valuesNightV31Directory = resDirectory.resolve("values-night-v31")
        if (!valuesNightV31Directory.isDirectory) Files.createDirectories(valuesNightV31Directory.toPath())

        listOf(valuesV31Directory, valuesNightV31Directory).forEach { it ->
            val colorsXml = it.resolve("colors.xml")

            if(!colorsXml.exists()) {
                FileWriter(colorsXml).use {
                    it.write("<?xml version=\"1.0\" encoding=\"utf-8\"?><resources></resources>")
                }
            }
        }

        context.xmlEditor["res/values/colors.xml"].use { editor ->
            val document = editor.file

            mapOf(
               "deep_transparent_twitter_blue" to "@android:color/system_accent1_200",
                "link_color" to "@color/twitter_blue",
                "ps_blue" to "@color/twitter_blue",
                "ps_main_primary" to "@color/twitter_blue",
                "ps_main_secondary" to "@android:color/system_accent1_400",
                "ps__twitter_blue" to "@color/twitter_blue",
                "tab_indicator" to "@color/twitter_blue",
                "text_blue" to "@color/twitter_blue",
                "twitter_blue" to "@android:color/system_accent1_400",
                "twitter_blue_fill_pressed" to "@android:color/system_accent1_300",
                "twitter_blue_opacity_30" to "@android:color/system_accent1_100",
                "twitter_blue_opacity_50" to "@android:color/system_accent1_200",
                "twitter_blue_opacity_58" to "@android:color/system_accent1_300"
            ).forEach { (k, v) ->
                val colorElement = document.createElement("color")

                colorElement.setAttribute("name", k)
                colorElement.textContent = v

                document.getElementsByTagName("resources").item(0).appendChild(colorElement)
            }
        }

        context.xmlEditor["res/values/styles.xml"].use { editor ->
            val document = editor.file

            val paletteDimMap = mapOf(
               "abstractColorCellBackground" to "#ff121314",
                "abstractColorCellBackgroundTranslucent" to "@color/black_opacity_50",
                "abstractColorDeepGray" to "#ff7c838a",
                "abstractColorDivider" to "#ff2f3336",
                "abstractColorFadedGray" to "#ff202327",
                "abstractColorFaintGray" to "#ff15181c",
                "abstractColorHighlightBackground" to "#ff15181c",
                "abstractColorLightGray" to "#ff2f3336",
                "abstractColorLink" to "@color/twitter_blue",
                "abstractColorMediumGray" to "#ff505457",
                "abstractColorText" to "#ffd9d9d9",
                "abstractColorUnread" to "#ff1b2023",
                "abstractElevatedBackground" to "#ff1b2023",
                "abstractElevatedBackgroundShadow" to "@color/black_opacity_10"
            )
            
            val paletteDimSection = document.getElementsByTagName("style")
                .firstOrNull { it.attributes.getNamedItem("name")?.nodeValue == "PaletteDim" }

            paletteDimSection?.let { paletteDim ->
                paletteDimMap.forEach { (key, value) ->
                    val itemElements = paletteDim.childNodes
                        .filter { it.nodeType == Node.ELEMENT_NODE && it.nodeName == "item" }
                        .filter { it.attributes.getNamedItem("name")?.nodeValue == key }

                    itemElements.forEach { itemElement ->
                        itemElement.textContent = value
                    }
                }
            }
        }
    }
}
