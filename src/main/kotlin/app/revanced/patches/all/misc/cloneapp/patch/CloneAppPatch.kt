package app.revanced.patches.all.misc.cloneapp.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.*
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.util.microg.MicroGResourceHelper
import org.w3c.dom.Element

@Patch(include = false)
@Name("clone-app")
@Description("Clones the app.")
@Version("0.0.1")
class CloneAppPatch : ResourcePatch {
    override fun execute(context: ResourceContext): PatchResult {
        
        val fromPackageName = context.xmlEditor["AndroidManifest.xml"].use { dom ->
                val manifestNode = dom
                    .file
                    .getElementsByTagName("manifest")
                    .item(0) as Element

                manifestNode.getAttribute("package")
            }
            
        val toPackageName = packageName ?: fromPackageName.plus(".revanced")
        
        //overwrite
        val manifest = context["AndroidManifest.xml"].readText()
        context["AndroidManifest.xml"].writeText(
            manifest.replace(
                "package=\"$fromPackageName",
                "package=\"$toPackageName"
            ).replace(
                "android:label=\"@string/app_name",
                "android:label=\"$appName"
            ).replace(
                "android:label=\"@string/app_launcher_name",
                "android:label=\"$appName"
            ).replace(
                "<permission android:name=\"$fromPackageName",
                "<permission android:name=\"$toPackageName"
            ).replace(
                "android:authorities=\"$fromPackageName",
                "android:authorities=\"$toPackageName"
            )
        )
        
        return PatchResultSuccess()
    }

    companion object : OptionsContainer() {
        var packageName: String? by option(
            PatchOption.StringOption(
                key = "packageName",
                default = null,
                title = "Package name of the cloned app",
                description = "Cloned package name. Defaults to appending '.revanced'.",
            )
        )

        var appName: String? by option(
            PatchOption.StringOption(
                key = "appName",
                default = "@string/app_name",
                title = "Name of the cloned app",
                description = "The name of the cloned app on the home screen.",
            )
        )
    }
}
