package app.revanced.patches.youtube.layout.theme.patch

import app.revanced.patcher.ResourceContext
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.arsc.Style
import app.revanced.patcher.arsc.integer
import app.revanced.patcher.arsc.reference
import app.revanced.patcher.patch.*
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.youtube.layout.theme.annotations.ThemeCompatibility
import app.revanced.util.resources.ResourceUtils.base
import app.revanced.util.resources.ResourceUtils.setMultiple
import app.revanced.util.resources.ResourceUtils.toColorResource

@Patch
@DependsOn([LithoThemePatch::class])
@Name("theme")
@Description("Applies a custom theme.")
@ThemeCompatibility
@Version("0.0.1")
class ThemePatch : ResourcePatch {
    override fun execute(context: ResourceContext) {
        val darkThemeBackgroundColor = darkThemeBackgroundColor!!
        val lightThemeBackgroundColor = lightThemeBackgroundColor!!
        val darkThemeSeekbarColor = darkThemeSeekbarColor!!

        context.base.apply {
            setMultiple("color", dark.toList(), darkThemeBackgroundColor.toColorResource(this))
            setMultiple("color", light.toList(), lightThemeBackgroundColor.toColorResource(this))
            set(
                "color",
                "inline_time_bar_colorized_bar_played_color_dark",
                darkThemeSeekbarColor.toColorResource(this)
            )

            // change the splash screen color
            set(
                "style", "Base.Theme.YouTube.Launcher", Style(
                    mapOf(
                        "android:windowSplashScreenBackground" to reference(this, "@android:color/black"),
                        "android:windowSplashScreenAnimatedIcon" to reference(this, "@drawable/avd_anim"),
                        "android:windowSplashScreenAnimationDuration" to integer(1000),
                    ),
                    parent = "@style/Theme.AppCompat.NoActionBar"
                ), configuration = "-night-v31"
            )
        }

        return PatchResult.Success
    }

    private val dark = arrayOf(
        "yt_black0",
        "yt_black1",
        "yt_black1_opacity95",
        "yt_black1_opacity98",
        "yt_black2",
        "yt_black3",
        "yt_black4",
        "yt_status_bar_background_dark",
        "material_grey_850"
    )

    private val light =
        arrayOf("yt_white1", "yt_white1_opacity95", "yt_white1_opacity98", "yt_white2", "yt_white3", "yt_white4")

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

        var darkThemeSeekbarColor: String? by option(
            PatchOption.StringOption(
                key = "darkThemeSeekbarColor",
                default = "#ffff0000",
                title = "Dark theme seekbar color",
                description = "The background color of the seekbar of the dark theme. Leave empty for default color.",
            )
        )
    }
}
