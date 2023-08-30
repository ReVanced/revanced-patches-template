package app.revanced.patches.grindr.microg.patch.resource

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patches.shared.settings.preference.impl.Preference
import app.revanced.patches.shared.settings.preference.impl.StringResource

import org.w3c.dom.Element
import org.w3c.dom.Node

class GooglePlayServicesManifestResourcePatch : ResourcePatch {
    override fun execute(context: ResourceContext): PatchResult {
        context.xmlEditor["AndroidManifest.xml"].use {
            val applicationNode = it
                .file
                .getElementsByTagName("application")
                .item(0)

            applicationNode.adoptChild("meta-data") {
                setAttribute("android:name", "com.google.android.gms.version")
                setAttribute("android:value", "@integer/google_play_services_version")
            }
        }

        return PatchResultSuccess()
    }

    private fun Node.adoptChild(tagName: String, block: Element.() -> Unit) {
        val child = ownerDocument.createElement(tagName)
        child.block()
        appendChild(child)
    }
}