package app.revanced.patches.all.misc.debugging.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.ResourceContext
import app.revanced.patcher.patch.*
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.util.resources.ResourceUtils.manifestEditor
import org.w3c.dom.Element

@Patch(false)
@Name("Enable android debugging")
@Description("Enables Android debugging capabilities.")
@Version("0.0.1")
class EnableAndroidDebuggingPatch : ResourcePatch {
    override suspend fun execute(context: ResourceContext) {
        context.manifestEditor().use { dom ->
            val applicationNode = dom
                .file
                .getElementsByTagName("application")
                .item(0) as Element

            // set application as debuggable
            applicationNode.setAttribute("android:debuggable", "true")
        }

    }

}
