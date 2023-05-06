package app.revanced.patches.all.misc.packagename.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.ResourceContext
import app.revanced.patcher.apk.Apk
import app.revanced.patcher.patch.*
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.util.resources.ResourceUtils.base
import app.revanced.util.resources.ResourceUtils.editText
import app.revanced.util.resources.ResourceUtils.manifestEditor
import org.w3c.dom.Element

@Patch(false)
@Name("change-package-name")
@Description("Changes the package name.")
@Version("0.0.1")
class ChangePackageNamePatch : ResourcePatch {
    override fun execute(context: ResourceContext) {
        packageName?.let { packageName ->
            val packageNameRegex = Regex("^[a-z]\\w*(\\.[a-z]\\w*)+\$")
            if (!packageName.matches(packageNameRegex))
                throw PatchException("Invalid package name")

            val originalPackageName = context.manifestEditor().use { editor ->
                val manifest = editor.file.getElementsByTagName("manifest").item(0) as Element
                manifest.getAttribute("package")
            }

            if (!originalPackageName.matches(packageNameRegex))
                throw PatchException("Failed to get the original package name")

            context.base.openFile(Apk.manifest).editText {
                it.replace(originalPackageName, packageName)
            }

        } ?: throw PatchException("No package name provided")

    }

    companion object : OptionsContainer() {
        var packageName: String? by option(
            PatchOption.StringOption(
                key = "packageName",
                default = "",
                title = "Package name",
                description = "The name of the package to rename of the app.",
            )
        )
    }
}