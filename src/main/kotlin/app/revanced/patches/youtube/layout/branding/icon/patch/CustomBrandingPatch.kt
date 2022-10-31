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
import org.w3c.dom.Element
import java.io.File
import java.nio.file.Files

@Patch
@DependsOn([FixLocaleConfigErrorPatch::class])
@Name("custom-branding")
@Description("Changes the launcher name and icon.")
@CustomBrandingCompatibility
@Version("0.0.1")
class CustomBrandingPatch : ResourcePatch {
    override fun execute(context: ResourceContext): PatchResult {
        context.xmlEditor["res/values/strings.xml"].use { editor ->
            editor.file.getElementsByTagName("string").let { strings ->
                for (i in 0 until strings.length) {
                    val node = strings.item(i)
                    if (node !is Element || node.getAttribute("name") != "application_name") continue

                    // if the name is the default name, append it before the original app name
                    if (appName == DEFAULT_NAME) node.textContent = "$DEFAULT_NAME ${node.textContent}"
                    // otherwise replace the name with the new name
                    node.textContent = appName

                    break
                }
            }
        }

        // determine the naming of the files
        val naming = context["res/mipmap-mdpi"].listFiles()?.first {
            it.name.startsWith("adaptiveproduct_")
        }.let {
            it?.nameWithoutExtension?.substringAfter("adaptiveproduct_")?.substringBefore("_background")
        } ?: return PatchResultError("Could not determine the naming scheme for the icons.")

        fun copyResources(resourceGroups: List<ResourceUtils.ResourceGroup>) {
            iconPath?.let { iconPathString ->
                val iconPath = File(iconPathString)
                val resourceDirectory = context["res"]

                resourceGroups.forEach { group ->
                    val fromDirectory = iconPath.resolve(group.resourceDirectoryName)
                    val toDirectory = resourceDirectory.resolve(group.resourceDirectoryName)

                    group.resources.forEach { iconFileName ->
                        Files.write(
                            toDirectory.resolve(iconFileName.replace(ICON_FILE_NAME_PLACEHOLDER, naming)).toPath(),
                            fromDirectory.resolve(iconFileName).readBytes()
                        )
                    }
                }
            } ?: resourceGroups.forEach { context.copyResources("branding", it) }
        }

        val iconResourceFileNames = arrayOf(
            "adaptiveproduct_${ICON_FILE_NAME_PLACEHOLDER}_background_color_108",
            "adaptiveproduct_${ICON_FILE_NAME_PLACEHOLDER}_foreground_color_108",
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
        private const val DEFAULT_NAME = "ReVanced"
        private const val ICON_FILE_NAME_PLACEHOLDER = "PLACEHOLDER"

        private var appName: String? by option(
            PatchOption.StringOption(
                key = "appName",
                default = DEFAULT_NAME,
                title = "Application Name",
                description = "The name of the YouTube app on the home screen.",
                required = true
            )
        )

        private var iconPath: String? by option(
            PatchOption.StringOption(
                key = "iconPath",
                default = null,
                title = "App Icon Path",
                description = "A path containing mipmap resource folders with icons.",
            )
        )
    }
}
