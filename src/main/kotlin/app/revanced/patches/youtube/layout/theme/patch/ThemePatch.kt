package app.revanced.patches.youtube.layout.theme.patch

import app.revanced.patcher.annotation.*
import app.revanced.patcher.data.impl.ResourceData
import app.revanced.patcher.patch.OptionsContainer
import app.revanced.patcher.patch.PatchOption
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.patch.impl.ResourcePatch
import app.revanced.patches.youtube.layout.theme.Themes
import app.revanced.patches.youtube.misc.manifest.patch.FixLocaleConfigErrorPatch
import org.w3c.dom.Element

@Patch
@DependsOn([FixLocaleConfigErrorPatch::class])
@Name("theme")
@Description("Enables a custom theme.")
@Compatibility([Package("com.google.android.youtube")])
@Version("0.0.1")
class ThemePatch : ResourcePatch() {
    override fun execute(data: ResourceData): PatchResult {
        val theme = Themes.valueOf(theme)

        data.xmlEditor["res/values/colors.xml"].use { editor ->
            val resourcesNode = editor.file.getElementsByTagName("resources").item(0) as Element

            for (i in 0 until resourcesNode.childNodes.length) {
                val node = resourcesNode.childNodes.item(i)
                if (node !is Element) continue

                val element = resourcesNode.childNodes.item(i) as Element
                element.textContent = theme.applier(element.getAttribute("name")) ?: continue
            }
        }

        return PatchResultSuccess()
    }

    companion object : OptionsContainer() {
        private var theme: String by PatchOption.StringListOption(
            key = "theme",
            default = null,
            options = Themes.names,
            title = "Theme",
            description = "Select a theme.",
            required = true
        )
    }
}