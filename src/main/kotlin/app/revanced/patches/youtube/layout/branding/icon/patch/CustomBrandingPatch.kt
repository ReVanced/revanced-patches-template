package app.revanced.patches.youtube.layout.branding.icon.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.*
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.youtube.layout.branding.icon.annotations.CustomBrandingCompatibility
import app.revanced.patches.youtube.misc.manifest.patch.FixLocaleConfigErrorPatch
import app.revanced.util.resources.ResourceUtils
import app.revanced.util.resources.ResourceUtils.copyResources
import java.io.File
import java.nio.file.Files

@Patch
@DependsOn([FixLocaleConfigErrorPatch::class])
@Name("custom-branding")
@Description("Changes the YouTube / YT Music launcher icon and name to your choice (defaults to ReVanced).")
@CustomBrandingCompatibility
@Version("0.0.1")
class CustomBrandingPatch : ResourcePatch {
    override fun execute(context: ResourceContext): PatchResult {
        val manifest = context["AndroidManifest.xml"]

        val is_music = manifest.readText().contains("package=\"com.google.android.apps.youtube.music");

        // change the name of the app
        manifest.writeText(
            manifest.readText()
                .replace(
                    "android:label=\"@string/application_name",
                    "android:label=\"${ if (is_music) musicName else youtubeName}"
                )
        )


        fun copyResources(resourceGroups: List<ResourceUtils.ResourceGroup>) {
            val iconPath = if (is_music) musicIconPath else youtubeIconPath
            val default = if (is_music) "branding/music" else "branding"

            iconPath?.let { iconPathString ->
                val iconPath = File(iconPathString)
                val resourceDirectory = context["res"]

                resourceGroups.forEach { group ->
                    val fromDirectory = iconPath.resolve(group.resourceDirectoryName)
                    val toDirectory = resourceDirectory.resolve(group.resourceDirectoryName)

                    group.resources.forEach { iconFileName ->
                        Files.write(
                            toDirectory.resolve(iconFileName).toPath(),
                            fromDirectory.resolve(iconFileName).readBytes()
                        )
                    }
                }
            } ?: resourceGroups.forEach { context.copyResources(default, it) }
        }

        val appName = if (is_music) "youtube_music" else "youtube" 

        val iconResourceFileNames = arrayOf(
            "adaptiveproduct_${appName}_background_color_108",
            "adaptiveproduct_${appName}_foreground_color_108",
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

        return PatchResultSuccess()
    }

    companion object : OptionsContainer() {
        private var youtubeName: String? by option(
            PatchOption.StringOption(
                key = "youtubeAppName",
                default = "YouTube ReVanced",
                title = "Application Name",
                description = "The name of the YouTube app on the home screen.",
                required = true
            )
        )
        
        private var musicName: String? by option(
            PatchOption.StringOption(
                key = "musicAppName",
                default = "YouTube Music ReVanced",
                title = "Application Name",
                description = "The name of the YouTube Music app on the home screen.",
                required = true
            )
        )

        private var youtubeIconPath: String? by option(
            PatchOption.StringOption(
                key = "youtubeIconPath",
                default = null,
                title = "YouTube App Icon Path",
                description = "A path containing mipmap resource folders with icons."
            )
        )

        private var musicIconPath: String? by option(
            PatchOption.StringOption(
                key = "musicIconPath",
                default = null,
                title = "YouTube Music App Icon Path",
                description = "A path containing mipmap resource folders with icons."
            )
        )
    }
}
