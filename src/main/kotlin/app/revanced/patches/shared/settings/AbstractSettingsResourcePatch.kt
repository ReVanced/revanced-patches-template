package app.revanced.patches.shared.settings

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.util.DomFileEditor
import app.revanced.patches.shared.settings.preference.BasePreference
import app.revanced.patches.shared.settings.preference.BaseResource
import app.revanced.patches.shared.settings.preference.addPreference
import app.revanced.patches.shared.settings.preference.addResource
import app.revanced.patches.shared.settings.preference.impl.ArrayResource
import app.revanced.util.resources.ResourceUtils
import app.revanced.util.resources.ResourceUtils.copyResources
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
        arraysEditor = context.xmlEditor["res/values/arrays.xml"]
        revancedPreferencesEditor = context.xmlEditor["res/xml/$preferenceFileName.xml"]
    }

    internal companion object {
        private var revancedPreferenceNode: Node? = null
        private var arraysNode: Node? = null

        private var revancedPreferencesEditor: DomFileEditor? = null
            set(value) {
                field = value
                revancedPreferenceNode = value.getNode("PreferenceScreen")
            }
        private var arraysEditor: DomFileEditor? = null
            set(value) {
                field = value
                arraysNode = value.getNode("resources")
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
                is ArrayResource -> addArray(this)
                else -> throw NotImplementedError("Unsupported resource type")
            }
        }

        internal fun DomFileEditor?.getNode(tagName: String) =
            this!!.file.getElementsByTagName(tagName).item(0)
    }

    override fun close() {
        revancedPreferencesEditor?.close()
        arraysEditor?.close()
    }
}