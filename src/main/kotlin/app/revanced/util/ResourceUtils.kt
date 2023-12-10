package app.revanced.util

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.PatchException
import app.revanced.patcher.util.DomFileEditor
import org.w3c.dom.Node
import java.nio.file.Files
import java.nio.file.StandardCopyOption

private val classLoader = object {}.javaClass.classLoader

/**
 * Recursively traverse the DOM tree starting from the given root node.
 *
 * @param action function that is called for every node in the tree.
 */
fun Node.doRecursively(action: (Node) -> Unit) {
    action(this)
    for (i in 0 until this.childNodes.length) this.childNodes.item(i).doRecursively(action)
}

/**
 * Copy resources from the current class loader to the resource directory.
 *
 * @param sourceResourceDirectory The source resource directory name.
 * @param resources The resources to copy.
 * @param replaceDestinationFiles If any existing destination files should be replaced.
 *                                If set to false, an exception is thrown if any source file
 *                                would overwrite an existing file.
 */
fun ResourceContext.copyResources(sourceResourceDirectory: String,
                                  vararg resources: ResourceGroup,
                                  replaceDestinationFiles: Boolean = true) {
    val targetResourceDirectory = this["res"]

    for (resourceGroup in resources) {
        resourceGroup.resources.forEach { resource ->
            val resourceFile = "${resourceGroup.resourceDirectoryName}/$resource"
            val resourcePath = "$sourceResourceDirectory/$resourceFile"
            val resourceAsStream = classLoader.getResourceAsStream(resourcePath)
                    ?: throw PatchException("Could not find resource: $resourcePath")
            Files.copy(
                resourceAsStream!!,
                targetResourceDirectory.resolve(resourceFile).toPath(),
                *(if (replaceDestinationFiles) arrayOf(StandardCopyOption.REPLACE_EXISTING) else emptyArray())
            )
        }
    }
}

/**
 * Resource names mapped to their corresponding resource data.
 * @param resourceDirectoryName The name of the directory of the resource.
 * @param resources A list of resource names.
 */
class ResourceGroup(val resourceDirectoryName: String, vararg val resources: String)

/**
 * Iterate through the children of a node by its tag.
 * @param resource The xml resource.
 * @param targetTag The target xml node.
 * @param callback The callback to call when iterating over the nodes.
 */
fun ResourceContext.iterateXmlNodeChildren(
    resource: String,
    targetTag: String,
    callback: (node: Node) -> Unit
) {
    val resourceAsStream = classLoader.getResourceAsStream(resource)
        ?: throw PatchException("Could not find resource: $resource")
    xmlEditor[resourceAsStream].use {
        val stringsNode = it.file.getElementsByTagName(targetTag).item(0).childNodes
        for (i in 1 until stringsNode.length - 1) callback(stringsNode.item(i))
    }
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