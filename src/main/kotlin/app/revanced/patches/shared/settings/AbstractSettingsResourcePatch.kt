package app.revanced.patches.shared.settings

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.PatchException
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.util.DomFileEditor
import app.revanced.patches.shared.settings.preference.BasePreference
import app.revanced.patches.shared.settings.preference.BaseResource
import app.revanced.patches.shared.settings.preference.addPreference
import app.revanced.patches.shared.settings.preference.addResource
import app.revanced.patches.shared.settings.preference.impl.ArrayResource
import app.revanced.util.resources.ResourceUtils
import app.revanced.util.resources.ResourceUtils.copyResources
import org.w3c.dom.Document
import org.w3c.dom.Node
import java.io.Closeable

/**
 * Abstract settings resource patch
 *
 * @param preferenceFileName Name of the settings preference xml file
 * @param sourceDirectory Source directory to copy the preference template from
 */
abstract class AbstractSettingsResourcePatch(
    private val preferenceFileName: String,
    private val sourceDirectory: String,
) : ResourcePatch(), Closeable {
    override fun execute(context: ResourceContext) {
        /*
         * used for self-restart
         * TODO: do this only, when necessary
         */
        context.xmlEditor["AndroidManifest.xml"].use { editor ->
            editor.file.getElementsByTagName("manifest").item(0).also {
                it.appendChild(it.ownerDocument.createElement("uses-permission").also { element ->
                    element.setAttribute("android:name", "android.permission.SCHEDULE_EXACT_ALARM")
                })
            }
        }

        /* copy preference template from source dir */
        context.copyResources(
            sourceDirectory,
            ResourceUtils.ResourceGroup(
                "xml", "$preferenceFileName.xml"
            )
        )

        /* prepare xml editors */
        stringsEditor = context.xmlEditor["res/values/strings.xml"]
        arraysEditor = context.xmlEditor["res/values/arrays.xml"]
        revancedPreferencesEditor = context.xmlEditor["res/xml/$preferenceFileName.xml"]
    }

    internal companion object {
        private var revancedPreferenceNode: Node? = null
        private var stringsNode: Node? = null
        private var arraysNode: Node? = null

        private var strings = mutableMapOf<String, StringResource>()

        private var revancedPreferencesEditor: DomFileEditor? = null
            set(value) {
                field = value
                revancedPreferenceNode = value.getNode("PreferenceScreen")
            }
        private var stringsEditor: DomFileEditor? = null
            set(value) {
                field = value
                stringsNode = value.getNode("resources")
            }
        private var arraysEditor: DomFileEditor? = null
            set(value) {
                field = value
                arraysNode = value.getNode("resources")
            }

        /**
         * Add a new string to the resources.  This does not support localization.
         *
         * @param identifier The key of the string.
         * @param value The value of the string.
         * @throws IllegalArgumentException if the string already exists.
         */
        fun addString(identifier: String, value: String, formatted: Boolean) =
            StringResource(identifier, value, formatted).include()

        /**
         * Verifies a String resource with the given key has been merged.
         * Should be used everywhere a String resource is referenced.
         *
         * @throws PatchException
         */
        internal fun validateStringIsMerged(key: String): String {
            if (!strings.contains(key))
                throw PatchException("Unknown String resource: '$key'  Merge patch strings before referencing.")
            return key
        }

        /**
         * Add an array to the resources.
         *
         * @param arrayResource The array resource to add.
         */
        fun addArray(arrayResource: ArrayResource) =
            arraysNode!!.addResource(arrayResource) { it.include() }

        /**
         * Add a preference to the settings.
         *
         * @param preference The preference to add.
         */
        fun addPreference(preference: BasePreference) =
            revancedPreferenceNode!!.addPreference(preference) { it.include() }

        /**
         * Add a new resource to the resources.
         *
         * @throws IllegalArgumentException if the resource already exists.
         */
        internal fun BaseResource.include() {
            when (this) {
                is StringResource -> {
                    if (strings.put(name, this) != null) {
                        throw PatchException("Tried to add duplicate string: $name")
                    }
                }
                is ArrayResource -> addArray(this)
                else -> throw NotImplementedError("Unsupported resource type")
            }
        }

        internal fun DomFileEditor?.getNode(tagName: String) = this!!.file.getElementsByTagName(tagName).item(0)
    }

    override fun close() {
        // merge all strings, skip duplicates
        strings.values.forEach {
            stringsNode!!.addResource(it)
        }

        revancedPreferencesEditor?.close()
        stringsEditor?.close()
        arraysEditor?.close()
    }
}

/**
 * Legacy code, used to import non translated default strings into the Strings.xml file.
 *
 * @param name The name of the string.
 * @param value The value of the string.
 * @param formatted If the string is formatted. If false, the attribute will be set.
 */
private class StringResource(
    name: String,
    val value: String,
    val formatted: Boolean = true
) : BaseResource(name, "string") {

    override fun serialize(ownerDocument: Document, resourceCallback: (BaseResource) -> Unit) =
        super.serialize(ownerDocument, resourceCallback).apply {
            // if the string is un-formatted, explicitly add the formatted attribute
            if (!formatted) setAttribute("formatted", "false")

            textContent = value
        }
}