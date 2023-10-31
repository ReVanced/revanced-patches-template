package app.revanced.patches.shared.strings

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.PatchException
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.util.DomFileEditor
import app.revanced.patches.shared.settings.AbstractSettingsResourcePatch.Companion.getNode
import app.revanced.patches.shared.settings.preference.BaseResource
import app.revanced.patches.shared.settings.preference.addResource
import app.revanced.util.resources.ResourceUtils
import app.revanced.util.resources.ResourceUtils.copyResources
import app.revanced.util.resources.ResourceUtils.iterateXmlNodeChildren
import org.w3c.dom.Document
import org.w3c.dom.Node
import java.io.Closeable
import java.util.jar.JarFile
import java.util.regex.Pattern

abstract class AbstractStringsResourcePatch(
    private var rootResourcePath : String
) : ResourcePatch(), Closeable {

    companion object {
        private var strings = mutableSetOf<String>()

        /**
         * Verifies a referenced string exists.
         *
         * Should be used everywhere string resources are referenced,
         * as missing resources will fail to compile and usually give no feedback to what's wrong.
         *
         * @return The string key.
         * @throws PatchException If the string does not exist.
         */
        internal fun assertStringExists(key: String): String {
            if (!strings.contains(key))
                throw PatchException("Unknown String resource: '$key'  Include patch strings before referencing.")
            return key
        }
    }

    private lateinit var resourceContext: ResourceContext
    private lateinit var stringsEditor: DomFileEditor
    private lateinit var stringsNode: Node

    override fun execute(context: ResourceContext) {
        resourceContext = context
        stringsEditor = context.xmlEditor["res/values/strings.xml"]
        stringsNode = stringsEditor.getNode("resources")
    }

    /**
     * Includes English Strings for a given patch.
     *
     * @param patchName Name of the patch strings xml file.
     */
    fun includePatchStrings(patchName: String) {
        resourceContext.iterateXmlNodeChildren("$rootResourcePath/$patchName.xml", "resources") {
            // TODO: figure out why this is needed
            if (!it.hasAttributes()) return@iterateXmlNodeChildren

            val attributes = it.attributes
            val key = attributes.getNamedItem("name")!!.nodeValue!!
            val value = it.textContent!!
            val formatted =
                !attributes.getNamedItem("formatted")?.nodeValue.equals("false", ignoreCase = true)

            addString(key, value, formatted)
        }
    }

    /**
     * Add a new English string to the resources.
     *
     * @param key The key of the string.
     * @param value The value of the string.
     * @throws PatchException if the string already exists.
     */
    internal fun addString(key: String, value: String, formatted: Boolean) {
        // Detect unescaped quotes that will throw generic AAPT errors
        // or will compile but not show up in the app as intended.
        if (value.contains(Regex("(?<!\\\\)['\"]")))
            throw PatchException("Unescaped quotes found in key: $key value: $value")
        if (!strings.add(key)) throw PatchException("Tried to add duplicate string: $key")

        stringsNode.addResource(StringResource(key, value, formatted))
    }

    /**
     * Copy localized strings from a given directory.
     */
    internal fun copyLocalizedStringFiles(directory: String) {
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
                    resourceContext.copyResources(
                        directory,
                        ResourceUtils.ResourceGroup(languageDirectory, "strings.xml"),
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

    override fun close() {
        stringsEditor.close()
    }
}

/**
 * Used to merge non translated English strings into the default Strings.xml file.
 *
 * @param name String key.
 * @param value String text value.
 * @param formatted If the text is formatted.
 */
private class StringResource(
    name: String,
    val value: String,
    val formatted: Boolean = true
) : BaseResource(name, "string") {

    override fun serialize(ownerDocument: Document) =
        super.serialize(ownerDocument).apply {
            // if the string is un-formatted, explicitly add the formatted attribute
            if (!formatted) setAttribute("formatted", "false")

            textContent = value
        }
}