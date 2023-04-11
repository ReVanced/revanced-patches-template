package app.revanced.patches.shared.settings.resource.patch

import app.revanced.patcher.DomFileEditor
import app.revanced.patcher.ResourceContext
import app.revanced.patcher.apk.Apk
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patches.shared.settings.preference.BasePreference
import app.revanced.patches.shared.settings.preference.IResource
import app.revanced.patches.shared.settings.preference.addPreference
import app.revanced.patches.shared.settings.preference.impl.ArrayResource
import app.revanced.patches.shared.settings.preference.impl.StringResource
import app.revanced.util.resources.ResourceUtils
import app.revanced.util.resources.ResourceUtils.copyResources
import org.w3c.dom.Node

/**
 * Abstract settings resource patch
 *
 * @param preferenceFileName Name of the settings preference xml file
 * @param sourceDirectory Source directory to copy the preference template from
 */
abstract class AbstractSettingsResourcePatch(
    private val preferenceFileName: String,
    private val sourceDirectory: String,
) : ResourcePatch {
    override fun execute(context: ResourceContext): PatchResult {
        /*
         * used for self-restart
         * TODO: do this only, when necessary
         */
        context.openEditor("AndroidManifest.xml").use { editor ->
            editor.file.getElementsByTagName("manifest").item(0).also {
                it.appendChild(it.ownerDocument.createElement("uses-permission").also { element ->
                    element.setAttribute("android:name", "android.permission.SCHEDULE_EXACT_ALARM")
                })
            }
        }
        base = context.apkBundle.base

        /* copy preference template from source dir */
        context.copyResources(
            sourceDirectory,
            ResourceUtils.ResourceGroup(
                "xml", "$preferenceFileName.xml"
            )
        )

        /* prepare xml editors */
        revancedPreferencesEditor = context.openEditor("res/xml/$preferenceFileName.xml")

        return PatchResult.Success
    }

    internal companion object {
        private var revancedPreferenceNode: Node? = null
        private var base: Apk? = null
        private var resources = mutableListOf<IResource>()

        private var revancedPreferencesEditor: DomFileEditor? = null
            set(value) {
                field = value
                revancedPreferenceNode = value.getNode("PreferenceScreen")
            }

        /**
         * Add a new string to the resources.
         *
         * @param identifier The key of the string.
         * @param value The value of the string.
         * @throws IllegalArgumentException if the string already exists.
         */
        fun addString(identifier: String, value: String) =
            StringResource(identifier, value).include()

        /**
         * Add an array to the resources.
         *
         * @param arrayResource The array resource to add.
         */
        fun addArray(arrayResource: ArrayResource) = arrayResource.include()

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
        internal fun IResource.include() {
            resources.add(this)
        }

        internal fun DomFileEditor?.getNode(tagName: String) = this!!.file.getElementsByTagName(tagName).item(0)
    }

    override fun close() {
        // merge all strings, skip duplicates
        /*
        strings.forEach {
            stringsNode!!.addResource(it)
        }
         */
        resources.forEach { base!!.setResource(it.type, it.name, it.patcherValue) }
        resources.clear()

        revancedPreferencesEditor?.close()
    }
}