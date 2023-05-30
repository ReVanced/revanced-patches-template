package app.revanced.util.resources

import app.revanced.patcher.DomFileEditor
import app.revanced.patcher.ResourceContext
import app.revanced.patcher.apk.Apk
import app.revanced.patcher.apk.ApkBundle
import app.revanced.patcher.apk.ResourceFile
import app.revanced.patcher.resource.Resource
import app.revanced.patcher.resource.StringResource
import app.revanced.patcher.resource.color
import app.revanced.patcher.resource.reference

internal object ResourceUtils {
    internal fun ResourceContext.mergeStrings(resources: Map<String, String>) =
        base.setGroup("string", resources.mapValues {
            StringResource(
                it.value
            )
        })

    internal fun ResourceFile.editText(block: (String) -> String) = use {
        it.contents = block(String(it.contents)).toByteArray()
    }

    internal fun String.toColorResource(resourceTable: ApkBundle.ResourceTable) =
        if (startsWith('@')) reference(resourceTable, this) else color(this)

    internal fun Apk.ResourceContainer.setMultiple(
        type: String,
        names: List<String>,
        value: Resource,
        configuration: String? = null
    ) = setGroup(
        type,
        names.associateWith { value }, configuration
    )

    internal fun Apk.ResourceContainer.setString(name: String, value: String) =
        set("string", name, StringResource(value))

    internal fun Apk.ResourceContainer.setStrings(resources: Map<String, String>) =
        setGroup("string", resources.mapValues {
            StringResource(
                it.value
            )
        })

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
                base.openFile("res/$resourceFile").use { file ->
                    file.outputStream().use {
                        classLoader.getResourceAsStream("$sourceResourceDirectory/$resourceFile")!!.copyTo(it)
                    }
                }
            }
        }
    }

    internal fun ResourceFile.takeIfExists() = if (!exists) {
        close()
        null
    } else this

    internal fun ResourceContext.resourceIdOf(type: String, name: String) =
        apkBundle.resources.resolveLocal(type, name).toLong()

    internal val ResourceContext.base get() = apkBundle.base.resources

    internal val ResourceContext.resourceTable get() = apkBundle.resources

    internal fun ResourceContext.manifestEditor() = base.openXmlFile(Apk.manifest)

    /**
     * Resource names mapped to their corresponding resource data.
     * @param resourceDirectoryName The name of the directory of the resource.
     * @param resources A list of resource names.
     */
    internal class ResourceGroup(val resourceDirectoryName: String, vararg val resources: String)

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
