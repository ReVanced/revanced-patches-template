package app.revanced.patches.youtube.layout.theme.resource

import app.revanced.arsc.resource.Style
import app.revanced.patcher.ResourceContext
import app.revanced.patcher.patch.*
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.arsc.resource.reference
import app.revanced.patcher.openXmlFile
import app.revanced.patches.shared.settings.preference.impl.InputType
import app.revanced.patches.shared.settings.preference.impl.StringResource
import app.revanced.patches.shared.settings.preference.impl.TextPreference
import app.revanced.patches.youtube.layout.seekbar.resource.SeekbarPreferencesPatch
import app.revanced.patches.youtube.layout.theme.bytecode.patch.ThemeBytecodePatch.Companion.darkThemeBackgroundColor
import app.revanced.patches.youtube.layout.theme.bytecode.patch.ThemeBytecodePatch.Companion.lightThemeBackgroundColor
import app.revanced.patches.youtube.layout.theme.bytecode.patch.ThemeBytecodePatch.Companion.splashScreenBackgroundColor
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch
import app.revanced.util.resources.ResourceUtils.base
import app.revanced.util.resources.ResourceUtils.resourceTable
import app.revanced.util.resources.ResourceUtils.setMultiple
import app.revanced.util.resources.ResourceUtils.toColorResource
import org.w3c.dom.Element

@DependsOn([SettingsPatch::class, SeekbarPreferencesPatch::class])
class ThemeResourcePatch : ResourcePatch {
    override suspend fun execute(context: ResourceContext) {
        SeekbarPreferencesPatch.addPreferences(
            TextPreference(
                "revanced_seekbar_color",
                StringResource("revanced_seekbar_color_title", "Seekbar color"),
                StringResource("revanced_seekbar_color_summary", "The color of the seekbar"),
                InputType.TEXT_CAP_CHARACTERS
            )
        )

        // Edit theme colors via resources.
        with(context.base) {
            darkThemeBackgroundColor?.let { setMultiple("color", dark, it.toColorResource(context.resourceTable)) }
            lightThemeBackgroundColor?.let { setMultiple("color", light, it.toColorResource(context.resourceTable)) }

            // Edit splash screen background color.
            splashScreenBackgroundColor?.let {
                val colorResourceId = set("color", COLOR_NAME, it.toColorResource(context.resourceTable))

                // change the splash screen color
                set(
                    "style", LAUNCHER_STYLE_NAME, Style(
                        mapOf(
                            "android:windowSplashScreenBackground" to reference(colorResourceId),
                        ),
                        parent = "@style/Theme.AppCompat.NoActionBar"
                    ), configuration = "-night-v31"
                )

                // Point to the splash screen background color.
                openXmlFile("res/drawable/quantum_launchscreen_youtube.xml").use { editor ->
                    val node = editor.file.getElementsByTagName("layer-list").item(0) as Element

                    val backgroundColorItem = node.childNodes.item(1) as Element
                    backgroundColorItem.apply {
                        setAttribute("android:drawable", "@color/$COLOR_NAME")
                    }
                }
            }
        }
    }
    private companion object {
        val dark = listOf(
            "yt_black0", "yt_black1", "yt_black1_opacity95", "yt_black1_opacity98", "yt_black2", "yt_black3",
            "yt_black4", "yt_status_bar_background_dark", "material_grey_850"
        )

        val light =
            listOf(
                "yt_white1", "yt_white1_opacity95", "yt_white1_opacity98",
                "yt_white2", "yt_white3", "yt_white4",
            )

        private const val LAUNCHER_STYLE_NAME = "Base.Theme.YouTube.Launcher"
        private const val COLOR_NAME = "splash_background_color"
    }
}
