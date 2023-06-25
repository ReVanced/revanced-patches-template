package app.revanced.patches.all.misc.debugging.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.*
import app.revanced.patcher.patch.annotations.Patch
import org.w3c.dom.Element

@Patch(false)
@Name("enable-android-debugging")
@Description("Enables Android debugging capabilities.")
@Version("0.0.1")
class EnableAndroidDebuggingPatch : ResourcePatch {
    override fun execute(context: ResourceContext): PatchResult {
        context.xmlEditor["AndroidManifest.xml"].use { dom ->
            val applicationNode = dom
                .file
                .getElementsByTagName("application")
                .item(0) as Element

            // set application as debuggable
            applicationNode.setAttribute("android:debuggable", "true")
        }

        return PatchResultSuccess()
    }

}
