package app.revanced.patches.all.misc.packagename.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.Patch
import org.w3c.dom.Element

@Patch()
@Name("change-packagename")
@Description("Changes the package name of the application.")
@Version("0.0.1")
class PackageNamePatch : ResourcePatch {
    override fun execute(context: ResourceContext): PatchResult {
        var fromPackageName = ""
        context.xmlEditor["AndroidManifest.xml"].use { dom ->
            val applicationNode = dom
                .file
                .getElementsByTagName("manifest")
                .item(0) as Element

            fromPackageName = applicationNode.getAttribute("package")
        }

        val toPackageName = fromPackageName + ".revanced"

        val manifest = context["AndroidManifest.xml"].readText()
        context["AndroidManifest.xml"].writeText(
            manifest.replace(
                fromPackageName,
                toPackageName
            )
        )

        return PatchResultSuccess()
    }

}
