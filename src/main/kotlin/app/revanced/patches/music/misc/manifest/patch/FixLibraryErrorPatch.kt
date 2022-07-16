package app.revanced.patches.music.misc.manifest.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.impl.ResourceData
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.impl.ResourcePatch
import app.revanced.patches.music.misc.manifest.annotations.FixLibraryErrorCompatibility
import org.w3c.dom.Element

@Name("library-fix")
@Description("Fixes missing library errors when running the app by patching the manifest file.")
@FixLibraryErrorCompatibility
@Version("0.0.1")
class FixLibraryErrorPatch : ResourcePatch() {
    override fun execute(data: ResourceData): PatchResult {
        // create an xml editor instance
        data.xmlEditor["AndroidManifest.xml"].use {
            // edit the application nodes attribute...
            val applicationNode = it
                .file
                .getElementsByTagName("application")
                .item(0) as Element
            // by replacing the attributes name
            applicationNode.setAttribute("android:extractNativeLibs", "true")

        }

        return PatchResultSuccess()
    }
}
