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
import org.w3c.dom.Document
import org.w3c.dom.Element

@Patch
@DependsOn([LithoThemePatch::class, FixLocaleConfigErrorPatch::class])
@Name("theme")
@Description("Applies a custom theme.")
@ThemeCompatibility
@Version("0.0.1")
class ThemePatch : ResourcePatch {
    override fun execute(context: ResourceContext): PatchResult {
        val darkThemeBackgroundColor = darkThemeBackgroundColor!!
        val lightThemeBackgroundColor = lightThemeBackgroundColor!!

        val android11ValuesFolder = "values"
        val android12ValuesFolder = "values-v31"
        val drawableFolders: List<String> = listOf(
            "drawable",
            "drawable-sw600dp"
        )
        val splashScreenColorAttributeName = "splashScreenColor"

        fun changeAppBackgroundColorValues(indexedNode: Element) {
            /**
             * Get all node elements with a specific value (example: "yt_white1" or "yt_black1")
             * then change it with the content of field 'lightThemeBackgroundColor' or 'darkThemeBackgroundColor'
             */
            val nodeAttribute = indexedNode.getAttribute("name")

            indexedNode.textContent = when (nodeAttribute) {
                "yt_black0", "yt_black1", "yt_black1_opacity95", "yt_black1_opacity98",
                "yt_black2", "yt_black3", "yt_black4", "yt_status_bar_background_dark",
                "material_grey_850" -> darkThemeBackgroundColor

                "yt_white1", "yt_white1_opacity95", "yt_white1_opacity98", "yt_white2",
                "yt_white3", "yt_white4" -> lightThemeBackgroundColor

                else -> nodeAttribute
            }
        }
        fun addNewColorAttribute(rootNode: Element, file: Document) {
            /**
             * Create a new node element of type 'attr'
             * with attributes: 'format' (and its value = 'reference') & 'name' (and its value = 'splashScreenColor').
             */
            val newColorAttribute: Element = file.createElement("attr").apply {
                setAttribute("format", "reference")
                setAttribute("name", splashScreenColorAttributeName)
            }

            /**
             * Add created node element after 'rootNode', into xml file
             */
            rootNode.appendChild(newColorAttribute)
        }
        fun addColorAttributeInStyle(indexedNode: Element, file: Document, filePathIndex: Int) {
            val nodeAttributeName = indexedNode.getAttribute("name")

            /**
             * Create a new node element of type 'item'
             * with the attribute: 'name', and its value based on the field 'filePathIndex':
             *
             *      '/values/styles.xml' = 'splashScreenColor'
             *      '/values-v31/styles.xml' = 'android:windowSplashScreenBackground'
             *
             * @field filePathIndex The current styles.xml path, indexed by the method 'forEachIndexed()'
             */
            file.createElement("item").apply {
                setAttribute(
                    "name",
                    when (filePathIndex) {
                        0 -> splashScreenColorAttributeName
                        1 -> "android:windowSplashScreenBackground"
                        else -> "null"
                    }
                )

                /**
                 * Fill the value of created node element with the field 'lightThemeBackgroundColor' or 'darkThemeBackgroundColor'
                 * only if the name of 'indexedNode' is equal to the field 'nodeAttributeName'
                 */
                appendChild(
                    file.createTextNode(
                        when (filePathIndex) {
                            0 -> when (nodeAttributeName) {
                                "Base.Theme.YouTube.Launcher.Dark" -> darkThemeBackgroundColor
                                "Base.Theme.YouTube.Launcher.Light" -> lightThemeBackgroundColor
                                else -> "null"
                            }
                            1 -> when (nodeAttributeName) {
                                "Base.Theme.YouTube.Launcher" -> "?attr/$splashScreenColorAttributeName"
                                else -> "null"
                            }
                            else -> "null"
                        }
                    )
                )

                /**
                 * Add the created node element after 'indexedNode', into xml file
                 * only if the value of new node element is not equal to 'null'
                 */
                if (this.textContent != "null")
                    indexedNode.appendChild(this)
            }
        }
        fun changeSplashScreenBackgroundColor(rootNode: Element) {
            /**
             * Check if a node element with attribute 'android:drawable' exists
             * then change its value to '?attr/splashScreenColor'
             */
            if (rootNode.attributes.getNamedItem("android:drawable") != null)
                rootNode.setAttribute("android:drawable", "?attr/splashScreenColor")
        }

        fun editXMLFile(
            filePath: String,
            filePathIndex: Int,
            elementsToSearchName: String,
            patchIndex: Int
        ) {
            if (context[filePath].exists()) {
                context.xmlEditor[filePath].use { editor ->
                    val editorFile = editor.file
                    val rootNode = editorFile.getElementsByTagName(elementsToSearchName).item(0) as Element
                    val childNodes = rootNode.childNodes

                    for (i in 0 until childNodes.length) {
                        val indexedNode = childNodes.item(i) as? Element ?: continue

                        when(patchIndex) {
                            0 -> changeAppBackgroundColorValues(indexedNode)
                            1 -> addNewColorAttribute(rootNode, editorFile)
                            2 -> addColorAttributeInStyle(indexedNode, editorFile, filePathIndex)
                            3 -> changeSplashScreenBackgroundColor(rootNode)
                        }
                    }
                }
            }
        }

        editXMLFile(
            "res/$android11ValuesFolder/colors.xml",
            0,
            "resources",
            0
        )
        editXMLFile(
            "res/$android11ValuesFolder/attrs.xml",
            0,
            "resources",
            1
        )
        arrayOf(
            android11ValuesFolder,
            android12ValuesFolder
        ).forEachIndexed { filePathIndex, filePath ->
            editXMLFile(
                "res/$filePath/attrs.xml",
                filePathIndex,
                "resources",
                2
            )
        }
        drawableFolders.forEach { drawablePath ->
            editXMLFile(
                drawablePath,
                0,
                "item",
                3
            )
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
