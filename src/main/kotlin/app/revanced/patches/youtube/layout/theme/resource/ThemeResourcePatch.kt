package app.revanced.patches.youtube.layout.theme.resource

import app.revanced.patcher.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.resource.Style
import app.revanced.patcher.resource.integer
import app.revanced.patcher.resource.reference
import app.revanced.patches.shared.mapping.misc.patch.ResourceMappingPatch
import app.revanced.patches.shared.settings.preference.impl.InputType
import app.revanced.patches.shared.settings.preference.impl.StringResource
import app.revanced.patches.shared.settings.preference.impl.TextPreference
import app.revanced.patches.youtube.layout.theme.bytecode.patch.ThemeBytecodePatch.Companion.darkThemeBackgroundColor
import app.revanced.patches.youtube.layout.theme.bytecode.patch.ThemeBytecodePatch.Companion.lightThemeBackgroundColor
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch
import app.revanced.util.resources.ResourceUtils
import app.revanced.util.resources.ResourceUtils.base
import app.revanced.util.resources.ResourceUtils.copyResources
import app.revanced.util.resources.ResourceUtils.resourceIdOf
import app.revanced.util.resources.ResourceUtils.setMultiple
import app.revanced.util.resources.ResourceUtils.toColorResource
import org.w3c.dom.Element

@DependsOn([SettingsPatch::class, ResourceMappingPatch::class])
class ThemeResourcePatch : ResourcePatch {
    override fun execute(context: ResourceContext) {
        SettingsPatch.PreferenceScreen.LAYOUT.addPreferences(
            TextPreference(
                "revanced_seekbar_color",
                StringResource("revanced_seekbar_color_title", "Seekbar color"),
                InputType.STRING,
                "#FF0000",
                StringResource(
                    "revanced_seekbar_color_summary",
                    "The color of the seekbar for the dark theme."
                )
            ),
        )

        // Edit theme colors via bytecode.
        // For that the resource id is used in a bytecode patch to change the color.

        inlineTimeBarColorizedBarPlayedColorDarkId =
            context.resourceIdOf("id", "inline_time_bar_colorized_bar_played_color_dark")


        val darkThemeBackgroundColor = darkThemeBackgroundColor!!
        val lightThemeBackgroundColor = lightThemeBackgroundColor!!

        // Edit theme colors via resources.
        with(context.base) {
            setMultiple("color", dark, darkThemeBackgroundColor.toColorResource(this))
            setMultiple("color", light, lightThemeBackgroundColor.toColorResource(this))

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
    }

    internal companion object {
        var inlineTimeBarColorizedBarPlayedColorDarkId = -1L

        private val dark = listOf(
            "yt_black0", "yt_black1", "yt_black1_opacity95", "yt_black1_opacity98", "yt_black2", "yt_black3",
            "yt_black4", "yt_status_bar_background_dark", "material_grey_850"
        )

        private val light =
            listOf(
                "yt_white1", "yt_white1_opacity95", "yt_white1_opacity98",
                "yt_white2", "yt_white3", "yt_white4",
            )
    }
}
