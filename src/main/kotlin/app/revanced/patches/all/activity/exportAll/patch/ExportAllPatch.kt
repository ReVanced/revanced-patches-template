package app.revanced.patches.all.activity.exportAll.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.Patch

@Patch(false)
@Name("export-all-activities")
@Description("Makes an app export all of it's activites.")
@Version("0.0.1")
class ExportAllPatch : ResourcePatch {
    override fun execute(context: ResourceContext): PatchResult {
        context.xmlEditor["AndroidManifest.xml"].use { editor ->
            val document = editor.file
            val activities = document.getElementsByTagName("activity")

            for(i in 0..activities.length) {
                activities.item(i)?.let {
                    if (it.attributes.getNamedItem(FLAG) != null) {
                        if(it.attributes.getNamedItem(FLAG).nodeValue != "true") {
                            it.attributes.getNamedItem(FLAG).nodeValue = "true"
                        }
                        return@let
                    }

                    document.createAttribute(FLAG)
                        .apply { value = "true" }
                        .let(it.attributes::setNamedItem)
                }
            }
        }

        return PatchResultSuccess()
    }

    private companion object {
        const val FLAG = "android:exported"
    }
}
