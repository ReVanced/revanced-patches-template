package app.revanced.util.resources

import app.revanced.patcher.DomFileEditor
import app.revanced.patcher.ResourceContext
import app.revanced.patcher.apk.Apk
import app.revanced.patcher.arsc.ReferenceResource
import app.revanced.patcher.arsc.Resource
import app.revanced.patcher.arsc.color
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch
import app.revanced.patches.shared.settings.preference.impl.StringResource
import org.w3c.dom.Node

internal object ResourceUtils {

    /**
     * Merge strings. This manages [StringResource]s automatically.
     * @param host The hosting xml resource. Needs to be a valid strings.xml resource.
     */
    internal fun ResourceContext.mergeStrings(host: String) {
        this.iterateXmlNodeChildren(host, "resources") {
            // TODO: figure out why this is needed
            if (!it.hasAttributes()) return@iterateXmlNodeChildren

            val attributes = it.attributes
            val key = attributes.getNamedItem("name")!!.nodeValue!!
            val value = it.textContent!!

            SettingsPatch.addString(key, value)
        }
    }


    internal fun String.toColorResource() = if (startsWith('@')) toReference() else color(this)
    internal fun String.toReference() = ReferenceResource(this)
    internal fun Apk.setMultiple(type: String, names: List<String>, value: Resource, configuration: String? = null) = names.forEach { setResource(type, it, value, configuration) }

    /**
     * Copy resources from the current class loader to the resource directory.
     * @param sourceResourceDirectory The source resource directory name.
     * @param resources The resources to copy.
     */
    internal fun ResourceContext.copyResources(sourceResourceDirectory: String, vararg resources: ResourceGroup) {
        val classLoader = ResourceUtils.javaClass.classLoader

        for (resourceGroup in resources) {
            resourceGroup.resources.forEach { resource ->
                val resourceFile = "${resourceGroup.resourceDirectoryName}/$resource"
                apkBundle.base.openFile("res/$resourceFile").use { file ->
                    file.outputStream().use {
                        classLoader.getResourceAsStream("$sourceResourceDirectory/$resourceFile")!!.copyTo(it)
                    }
                }
            }
        }
    }

    internal val ResourceContext.base get() = apkBundle.base

    internal fun ResourceContext.manifestEditor() = base.openManifest().xmlEditor()

    internal fun Apk.Resources.openEditor(path: String) = file(path).xmlEditor()

    /**
     * Resource names mapped to their corresponding resource data.
     * @param resourceDirectoryName The name of the directory of the resource.
     * @param resources A list of resource names.
     */
    internal class ResourceGroup(val resourceDirectoryName: String, vararg val resources: String)

    /**
     * Iterate through the children of a node by its tag.
     * @param resource The xml resource.
     * @param targetTag The target xml node.
     * @param callback The callback to call when iterating over the nodes.
     */
    internal fun ResourceContext.iterateXmlNodeChildren(
        resource: String,
        targetTag: String,
        callback: (node: Node) -> Unit
    ) =
        openEditor(ResourceUtils.javaClass.classLoader.getResourceAsStream(resource)!!).use {
            val stringsNode = it.file.getElementsByTagName(targetTag).item(0).childNodes
            for (i in 1 until stringsNode.length - 1) callback(stringsNode.item(i))
        }


    /**
     * Copies the specified node of the source [DomFileEditor] to the target [DomFileEditor].
     * @param source the source [DomFileEditor].
     * @param target the target [DomFileEditor]-
     * @return AutoCloseable that closes the target [DomFileEditor]s.
     */
    fun String.copyXmlNode(source: DomFileEditor, target: DomFileEditor): AutoCloseable {
        val hostNodes = source.file.getElementsByTagName(this).item(0).childNodes

        val destinationResourceFile = target.file
        val destinationNode = destinationResourceFile.getElementsByTagName(this).item(0)

        for (index in 0 until hostNodes.length) {
            val node = hostNodes.item(index).cloneNode(true)
            destinationResourceFile.adoptNode(node)
            destinationNode.appendChild(node)
        }

        return AutoCloseable {
            source.close()
            target.close()
        }
    }
}
