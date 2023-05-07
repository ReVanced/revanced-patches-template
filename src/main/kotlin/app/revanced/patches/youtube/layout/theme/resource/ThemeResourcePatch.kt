package app.revanced.patches.youtube.layout.theme.resource

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultError
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patches.shared.mapping.misc.patch.ResourceMappingPatch
import app.revanced.patches.shared.settings.preference.impl.InputType
import app.revanced.patches.shared.settings.preference.impl.StringResource
import app.revanced.patches.shared.settings.preference.impl.TextPreference
import app.revanced.patches.youtube.layout.theme.bytecode.patch.ThemeBytecodePatch.Companion.darkThemeBackgroundColor
import app.revanced.patches.youtube.layout.theme.bytecode.patch.ThemeBytecodePatch.Companion.lightThemeBackgroundColor
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch
import app.revanced.util.resources.ResourceUtils
import app.revanced.util.resources.ResourceUtils.copyResources
import org.w3c.dom.Element

@DependsOn([SettingsPatch::class, ResourceMappingPatch::class])
class ThemeResourcePatch : ResourcePatch {
    override fun execute(context: ResourceContext): PatchResult {
        SettingsPatch.PreferenceScreen.LAYOUT.addPreferences(
            TextPreference(
                "revanced_seekbar_color",
                StringResource("revanced_seekbar_color_title", "Seekbar color"),
                InputType.TEXT_CAP_CHARACTERS,
                "#FF0000",
                StringResource(
                    "revanced_seekbar_color_summary",
                    "The color of the seekbar for the dark theme."
                )
            ),
        )

        // Edit theme colors via bytecode.
        // For that the resource id is used in a bytecode patch to change the color.

        inlineTimeBarColorizedBarPlayedColorDarkId = ResourceMappingPatch.resourceMappings
            .find { it.name == "inline_time_bar_colorized_bar_played_color_dark" }?.id
            ?: return PatchResultError("Could not find seekbar resource")


        val darkThemeBackgroundColor = darkThemeBackgroundColor!!
        val lightThemeBackgroundColor = lightThemeBackgroundColor!!

        // Edit theme colors via resources.
        context.xmlEditor["res/values/colors.xml"].use { editor ->
            val resourcesNode = editor.file.getElementsByTagName("resources").item(0) as Element

            for (i in 0 until resourcesNode.childNodes.length) {
                val node = resourcesNode.childNodes.item(i) as? Element ?: continue

                node.textContent = when (node.getAttribute("name")) {
                    "yt_black0", "yt_black1", "yt_black1_opacity95", "yt_black1_opacity98", "yt_black2", "yt_black3",
                    "yt_black4", "yt_status_bar_background_dark", "material_grey_850" -> darkThemeBackgroundColor

                    "yt_white1", "yt_white1_opacity95", "yt_white1_opacity98",
                    "yt_white2", "yt_white3", "yt_white4",
                    -> lightThemeBackgroundColor

                    else -> continue
                }
            }
        }

        // Copy the resource file to change the splash screen color.
        context.copyResources(
            "theme", ResourceUtils.ResourceGroup("values-night-v31", "styles.xml")
        )

        return PatchResultSuccess()
    }

    internal companion object {
        var inlineTimeBarColorizedBarPlayedColorDarkId = -1L
    }
}
