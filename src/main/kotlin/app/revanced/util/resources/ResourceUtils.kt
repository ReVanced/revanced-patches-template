
package app.revanced.util.resources

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.PatchException
import app.revanced.patcher.util.DomFileEditor
import app.revanced.patches.shared.settings.AbstractSettingsResourcePatch
import org.w3c.dom.Node
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.jar.JarFile
import java.util.regex.Pattern
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult


@Suppress("MemberVisibilityCanBePrivate")
object ResourceUtils {

    /**
     * Merge strings in the default Strings.xml file
     * @param host The hosting xml resource. Needs to be a valid strings.xml resource.
     */
    fun ResourceContext.mergeStrings(host: String) {
        this.iterateXmlNodeChildren(host, "resources") {
            // TODO: figure out why this is needed
            if (!it.hasAttributes()) return@iterateXmlNodeChildren

            val attributes = it.attributes
            val key = attributes.getNamedItem("name")!!.nodeValue!!
            val value = it.textContent!!

            val formatted = attributes.getNamedItem("formatted") == null

            AbstractSettingsResourcePatch.addString(key, value, formatted)
        }
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
    fun ResourceContext.copyResources(
        sourceResourceDirectory: String,
        vararg resources: ResourceGroup,
        replaceDestinationFiles: Boolean = true
    ) {
        val classLoader = ResourceUtils.javaClass.classLoader
        val targetResourceDirectory = this["res"]

        for (resourceGroup in resources) {
            resourceGroup.resources.forEach { resource ->
                // Create the target directory if it does not exist
                this[targetResourceDirectory.resolve(resourceGroup.resourceDirectoryName).absolutePath].mkdir()
                val resourceFile = "${resourceGroup.resourceDirectoryName}/$resource"
                Files.copy(
                    classLoader.getResourceAsStream("$sourceResourceDirectory/$resourceFile")!!,
                    targetResourceDirectory.resolve(resourceFile).toPath(),
                    *(if (replaceDestinationFiles) arrayOf(StandardCopyOption.REPLACE_EXISTING) else emptyArray())
                )
            }
        }
    }

    /**
     * Copy localized strings from a given directory.
     */
    internal fun copyLocalizedStringFiles(context: ResourceContext, directory: String) {
        val pattern = Pattern.compile("$directory/([-_a-zA-Z0-9]+)/strings\\.xml$")

        var jf: JarFile? = null
        try {
            jf = JarFile(this.javaClass.protectionDomain.codeSource.location.toURI().path)
            val entries = jf.entries()
            var foundElements = false
            while (entries.hasMoreElements()) {
                val match = pattern.matcher(entries.nextElement().name)
                if (match.find()) {
                    val languageDirectory = match.group(1)
                    context.copyResources(
                        directory,
                        ResourceGroup(languageDirectory, "strings.xml"),
                        replaceDestinationFiles = false // Destination files should not exist.
                    )
                    foundElements = true
                }
            }
            if (!foundElements) throw PatchException("could not find translated string files")
        } finally {
            jf?.close()
        }
    }

    /**
     * Used by Gradle task to merge the English patch strings into a single file for translations.
     */
    @JvmStatic
    fun main(args: Array<String>) {
        if (args.isEmpty() || args.size % 2 != 0) throw IllegalArgumentException()

        for (i in args.indices step 2) {
            mergeXMLFiles(args[i], args[i + 1])
        }
    }

    internal fun mergeXMLFiles(sourcePath: String, outputFilePath: String) {
        val sourceDirectory = File(sourcePath)
        val outputFile = File(outputFilePath)
        if (outputFile.exists()) {
            outputFile.delete()
        }
        if (!outputFile.parentFile.exists()) {
            outputFile.parentFile.mkdirs()
        }

        // Create a new Document to hold the merged content.
        val builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val mergedDocument = builder.newDocument()

        // Create the root element for the merged XML.
        val rootElement = mergedDocument.createElement("root")
        mergedDocument.appendChild(rootElement)

        rootElement.appendChild(rootElement.ownerDocument.createComment("This file is generated by a gradle task and should never be manually edited"))

        // Merge all xml files into the output document.
        sourceDirectory.listFiles { _, name -> name.endsWith(".xml") }?.sortedBy { it.name }
            ?.forEach { file ->
                rootElement.appendChild(rootElement.ownerDocument.createComment(file.nameWithoutExtension))

                val xmlDoc = builder.parse(file)
                val childNodes = xmlDoc.documentElement.childNodes
                for (i in 0 until childNodes.length) {
                    val child = childNodes.item(i)
                    val importedNode = rootElement.ownerDocument.importNode(child, true)
                    rootElement.appendChild(importedNode)
                }
            }

        // Save the merged XML to the output file.
        val transformer = TransformerFactory.newInstance().newTransformer()
        transformer.setOutputProperty(OutputKeys.INDENT, "yes")
        val source = DOMSource(mergedDocument)
        val result = StreamResult(outputFile)
        transformer.transform(source, result)
        println("Merged XML saved to: ${outputFile.absolutePath}")
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
        val resourceAsStream = ResourceUtils.javaClass.classLoader.getResourceAsStream(resource)
            ?: throw PatchException("Could not find resource file: $resource")
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
}