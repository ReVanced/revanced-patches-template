package app.revanced.patches.all.interaction.gestures.patch

import app.revanced.patcher.ResourceContext
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.patch.PatchException
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.util.resources.ResourceUtils.manifestEditor

@Patch(false)
@Name("predictive-back-gesture")
@Description("Enables the predictive back gesture introduced on Android 13.")
@Version("0.0.1")
class PredictiveBackGesturePatch : ResourcePatch {
    override fun execute(context: ResourceContext): PatchResult {
        context.manifestEditor().use { editor ->
            val document = editor.file

            with(document.getElementsByTagName("application").item(0)) {
                if (attributes.getNamedItem(FLAG) != null) return@with

                document.createAttribute(FLAG)
                    .apply { value = "true" }
                    .let(attributes::setNamedItem)

            }
        }

        return PatchResult.Success
    }

    private companion object {
        const val FLAG = "android:enableOnBackInvokedCallback"
    }
}
