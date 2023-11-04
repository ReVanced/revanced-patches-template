package app.revanced.patches.youtube.layout.branding

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patcher.patch.options.PatchOption.PatchExtensions.stringPatchOption
import app.revanced.util.resources.ResourceUtils
import app.revanced.util.resources.ResourceUtils.copyResources
import java.io.File
import java.nio.file.Files

@Patch(
    name = "Custom branding",
    description = "Changes the app icon and name to your choice (defaults to YouTube ReVanced and the ReVanced logo).",
    compatiblePackages = [
        CompatiblePackage("com.google.android.youtube")
    ],
    use = false
)
@Suppress("unused")
object CustomBrandingPatch : ResourcePatch() {
    private const val REVANCED_ICON = "ReVanced*Logo" // Can never be a valid path.
    private const val APP_NAME = "YouTube ReVanced"

    private val iconResourceFileNames = arrayOf(
        "adaptiveproduct_youtube_background_color_108",
        "adaptiveproduct_youtube_foreground_color_108",
        "ic_launcher",
        "ic_launcher_round"
    ).map { "$it.png" }.toTypedArray()

    private val mipmapDirectories = arrayOf(
        "xxxhdpi",
        "xxhdpi",
        "xhdpi",
        "hdpi",
        "mdpi"
    ).map { "mipmap-$it" }

    private var appName by stringPatchOption(
        key = "appName",
        default = APP_NAME,
        values = mapOf(
            "YouTube ReVanced" to APP_NAME,
            "YT" to "YT",
            "YouTube" to "YouTube",
        ),
        title = "App name",
        description = "The name of the app."
    )

    private var icon by stringPatchOption(
        key = "iconPath",
        default = REVANCED_ICON,
        values = mapOf("ReVanced Logo" to REVANCED_ICON),
        title = "App icon",
        description = """
            The path to a folder containing the following folders:

            ${mipmapDirectories.joinToString("\n") { "- $it" }}

            Each of these folders has to have the following files:

            ${iconResourceFileNames.joinToString("\n") { "- $it" }}
        """
            .split("\n")
            .joinToString("\n") { it.trimIndent() } // Remove the leading whitespace from each line.
            .trimIndent(), // Remove the leading newline.
    )

    override fun execute(context: ResourceContext) {
        icon?.let { icon ->
            // Change the app icon.
            mipmapDirectories.map { directory ->
                ResourceUtils.ResourceGroup(
                    directory, *iconResourceFileNames
                )
            }.let { resourceGroups ->
                if (icon != REVANCED_ICON) {
                    val path = File(icon)
                    val resourceDirectory = context["res"]

                    resourceGroups.forEach { group ->
                        val fromDirectory = path.resolve(group.resourceDirectoryName)
                        val toDirectory = resourceDirectory.resolve(group.resourceDirectoryName)

                        group.resources.forEach { iconFileName ->
                            Files.write(
                                toDirectory.resolve(iconFileName).toPath(),
                                fromDirectory.resolve(iconFileName).readBytes()
                            )
                        }
                    }
                } else resourceGroups.forEach { context.copyResources("branding", it) }
            }
        }

        appName?.let { name ->
            // Change the app name.
            val manifest = context["AndroidManifest.xml"]
            manifest.writeText(
                manifest.readText()
                    .replace(
                        "android:label=\"@string/application_name",
                        "android:label=\"$name"
                    )
            )
        }
    }
}
