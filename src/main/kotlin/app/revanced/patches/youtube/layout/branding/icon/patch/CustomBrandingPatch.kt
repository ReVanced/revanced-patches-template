package app.revanced.patches.youtube.layout.branding.icon.patch

import app.revanced.arsc.resource.ResourceContainer
import app.revanced.patcher.ResourceContext
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.apk.Apk
import app.revanced.patcher.patch.OptionsContainer
import app.revanced.patcher.patch.PatchOption
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.youtube.layout.branding.icon.annotations.CustomBrandingCompatibility
import app.revanced.util.resources.ResourceUtils
import app.revanced.util.resources.ResourceUtils.base
import app.revanced.util.resources.ResourceUtils.copyResources
import app.revanced.util.resources.ResourceUtils.editText
import java.io.File

@Patch
@Name("Custom branding")
@Description("Changes the YouTube launcher icon and name to your choice (defaults to ReVanced).")
@CustomBrandingCompatibility
@Version("0.0.1")
class CustomBrandingPatch : ResourcePatch {
    override suspend fun execute(context: ResourceContext) {
        fun copyResources(resourceGroups: Map<ResourceContainer, ResourceUtils.ResourceGroup>) {
            iconPath?.let { iconPathString ->
                val iconPath = File(iconPathString)

                resourceGroups.forEach { (apk, group) ->
                    val fromDirectory = iconPath.resolve(group.resourceDirectoryName)
                    val toDirectory = "res/${group.resourceDirectoryName}"

                    group.resources.forEach { iconFileName ->
                        apk.openFile("$toDirectory/$iconFileName").use {
                            it.contents = fromDirectory.resolve(iconFileName).readBytes()
                        }
                    }
                }
            } ?: resourceGroups.forEach { (apk, group) -> apk.copyResources("branding", group) }
        }

        val iconResourceFileNames = arrayOf(
            "adaptiveproduct_youtube_background_color_108",
            "adaptiveproduct_youtube_foreground_color_108",
            "ic_launcher",
            "ic_launcher_round"
        ).map { "$it.png" }.toTypedArray()

        fun createGroup(density: String) = context.apkBundle.query(density) to ResourceUtils.ResourceGroup(
            "mipmap-$density", *iconResourceFileNames
        )

        // change the app icon
        arrayOf("xxxhdpi", "xxhdpi", "xhdpi", "hdpi", "mdpi")
            .associate(::createGroup)
            .let(::copyResources)

        // change the name of the app
        context.base.openFile(Apk.manifest).editText {
            it.replace(
                "android:label=\"@string/application_name",
                "android:label=\"$appName"
            )
        }
    }

    companion object : OptionsContainer() {
        private var appName: String? by option(
            PatchOption.StringOption(
                key = "appName",
                default = "YouTube ReVanced",
                title = "Application Name",
                description = "The name of the application it will show on your home screen.",
                required = true
            )
        )

        private var iconPath: String? by option(
            PatchOption.StringOption(
                key = "iconPath",
                default = null,
                title = "App Icon Path",
                description = "A path containing mipmap resource folders with icons."
            )
        )
    }
}
