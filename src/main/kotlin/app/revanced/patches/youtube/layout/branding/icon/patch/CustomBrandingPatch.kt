package app.revanced.patches.youtube.layout.branding.icon.patch

import app.revanced.patcher.ResourceContext
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.apk.Apk
import app.revanced.patcher.patch.*
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.youtube.layout.branding.icon.annotations.CustomBrandingCompatibility
import app.revanced.util.resources.ResourceUtils
import app.revanced.util.resources.ResourceUtils.base
import app.revanced.util.resources.ResourceUtils.copyResources
import java.io.File

@Patch
@Name("custom-branding")
@Description("Changes the YouTube launcher icon and name to your choice (defaults to ReVanced).")
@CustomBrandingCompatibility
@Version("0.0.1")
class CustomBrandingPatch : ResourcePatch {
    override fun execute(context: ResourceContext) {
        fun copyResources(resourceGroups: List<ResourceUtils.ResourceGroup>) {
            iconPath?.let { iconPathString ->
                val iconPath = File(iconPathString)

                resourceGroups.forEach { group ->
                    val fromDirectory = iconPath.resolve(group.resourceDirectoryName)
                    val toDirectory = "res/${group.resourceDirectoryName}"

                    group.resources.forEach { iconFileName ->
                        context.base.openFile("$toDirectory/$iconFileName").use {
                            it.contents = fromDirectory.resolve(iconFileName).readBytes()
                        }
                    }
                }
            } ?: resourceGroups.forEach { context.copyResources("branding", it) }
        }

        val iconResourceFileNames = arrayOf(
            "adaptiveproduct_youtube_background_color_108",
            "adaptiveproduct_youtube_foreground_color_108",
            "ic_launcher",
            "ic_launcher_round"
        ).map { "$it.png" }.toTypedArray()

        fun createGroup(directory: String) = ResourceUtils.ResourceGroup(
            directory, *iconResourceFileNames
        )

        // change the app icon
        arrayOf("xxxhdpi", "xxhdpi", "xhdpi", "hdpi", "mdpi")
            .map { "mipmap-$it" }
            .map(::createGroup)
            .let(::copyResources)

        // change the name of the app
        context.base.openFile(Apk.manifest).use { manifest ->
            manifest.writeText(
                manifest.readText()
                    .replace(
                        "android:label=\"@string/application_name",
                        "android:label=\"$appName"
                    )
            )
        }

        return PatchResult.Success
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
