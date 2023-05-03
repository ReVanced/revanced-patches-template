package app.revanced.patches.twitter.misc.dynamiccolor.patch

import app.revanced.patcher.ResourceContext
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.apk.Apk
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.twitter.misc.dynamiccolor.annotations.DynamicColorCompatibility
import app.revanced.util.resources.ResourceUtils.base
import app.revanced.util.resources.ResourceUtils.toColorResource

@Patch
@Name("dynamic-color")
@Description("Replaces the default Twitter Blue with the users Material You palette.")
@DynamicColorCompatibility
@Version("0.0.1")
class DynamicColorPatch : ResourcePatch {
    override fun execute(context: ResourceContext) {
        fun Apk.Resources.setColors(configuration: String, resources: Map<String, String>) =
            setGroup("color", resources.mapValues { it.value.toColorResource(this) }, configuration)

        context.base.apply {
            setColors(
                "-v31", mapOf(
                    "ps__twitter_blue" to "@color/twitter_blue",
                    "ps__twitter_blue_pressed" to "@color/twitter_blue_fill_pressed",
                    "twitter_blue" to "@android:color/system_accent1_400",
                    "twitter_blue_fill_pressed" to "@android:color/system_accent1_300",
                    "twitter_blue_opacity_30" to "@android:color/system_accent1_100",
                    "twitter_blue_opacity_50" to "@android:color/system_accent1_200",
                    "twitter_blue_opacity_58" to "@android:color/system_accent1_300",
                    "deep_transparent_twitter_blue" to "@android:color/system_accent1_200",
                    "ic_launcher_background" to "#1DA1F2"
                )
            )

            setColors(
                "-night-v31", mapOf(
                    "twitter_blue" to "@android:color/system_accent1_200",
                    "twitter_blue_fill_pressed" to "@android:color/system_accent1_300",
                    "twitter_blue_opacity_30" to "@android:color/system_accent1_50",
                    "twitter_blue_opacity_50" to "@android:color/system_accent1_100",
                    "twitter_blue_opacity_58" to "@android:color/system_accent1_200",
                    "deep_transparent_twitter_blue" to "@android:color/system_accent1_200"
                )
            )
        }

    }
}
