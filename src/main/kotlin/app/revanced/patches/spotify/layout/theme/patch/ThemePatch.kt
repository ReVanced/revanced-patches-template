package app.revanced.patches.spotify.layout.theme.patch

import app.revanced.patcher.ResourceContext
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.patch.*
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.spotify.layout.theme.annotations.ThemeCompatibility
import app.revanced.util.resources.ResourceUtils.base
import app.revanced.util.resources.ResourceUtils.setMultiple
import app.revanced.util.resources.ResourceUtils.toColorResource

@Patch
@Name("spotify-theme")
@Description("Applies a custom theme.")
@ThemeCompatibility
@Version("0.0.1")
class ThemePatch : ResourcePatch {
    override fun execute(context: ResourceContext) {
        val resources = context.base

        resources.setGroup(
            "color", mapOf(
                "gray_7" to backgroundColor!!.toColorResource(resources),
                "dark_brightaccent_background_press" to accentPressedColor!!.toColorResource(resources)
            )
        )
        resources.setMultiple("color", listOf("dark_brightaccent_background_base", "dark_base_text_brightaccent", "green_light"), accentColor!!.toColorResource(resources))

        return PatchResult.Success
    }

    companion object : OptionsContainer() {
        var backgroundColor: String? by option(
            PatchOption.StringOption(
                key = "backgroundColor",
                default = "@android:color/black",
                title = "Background color",
                description = "The background color. Can be a hex color or a resource reference.",
            )
        )
        var accentColor: String? by option(
            PatchOption.StringOption(
                key = "accentColor",
                default = "#ff1ed760",
                title = "Accent color",
                description = "The accent color ('spotify green' by default). Can be a hex color or a resource reference.",
            )
        )
        var accentPressedColor: String? by option(
            PatchOption.StringOption(
                key = "accentPressedColor",
                default = "#ff169c46",
                title = "Pressed accent for the dark theme",
                description = "The color when accented buttons are pressed, by default slightly darker than accent. Can be a hex color or a resource reference.",
            )
        )
    }
}
