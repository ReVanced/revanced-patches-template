package app.revanced.patches.twitch.misc.settings.resource.patch

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.DomFileEditor
import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patches.shared.settings.BasePreference
import app.revanced.patches.shared.settings.IResource
import app.revanced.patches.shared.settings.addPreference
import app.revanced.patches.shared.settings.addResource
import app.revanced.patches.shared.settings.impl.ArrayResource
import app.revanced.patches.shared.settings.impl.StringResource
import app.revanced.patches.twitch.misc.settings.annotations.SettingsCompatibility
import app.revanced.patches.youtube.misc.manifest.patch.FixLocaleConfigErrorPatch
import app.revanced.util.resources.ResourceUtils
import app.revanced.util.resources.ResourceUtils.copyResources
import org.w3c.dom.Node

@Name("settings-resource-patch")
@SettingsCompatibility
@DependsOn([FixLocaleConfigErrorPatch::class])
@Version("0.0.1")
class SettingsResourcePatch : ResourcePatch {
    override fun execute(context: ResourceContext): PatchResult {

        /* required for self-restart in settings */
        context.xmlEditor["AndroidManifest.xml"].use { editor ->
            editor.file.getElementsByTagName("manifest").item(0).also {
                it.appendChild(it.ownerDocument.createElement("uses-permission").also { element ->
                    element.setAttribute("android:name", "android.permission.SCHEDULE_EXACT_ALARM")
                })
            }
        }

        /* copy resources */
        context.copyResources("twitch/settings", ResourceUtils.ResourceGroup(
            "xml", "revanced_prefs.xml" // template for new preferences
        ))

        revancedPreferencesEditor = context.xmlEditor["res/xml/revanced_prefs.xml"]
        stringsEditor = context.xmlEditor["res/values/strings.xml"]
        arraysEditor = context.xmlEditor["res/values/arrays.xml"]

        return PatchResultSuccess()
    }

    internal companion object {
        private var strings = mutableListOf<StringResource>()
        private var arrays = mutableListOf<ArrayResource>()

        private var revancedPreferenceNode: Node? = null
        private var stringsNode: Node? = null
        private var arraysNode: Node? = null

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
         * Add a new string to the resources.
         *
         * @param identifier The key of the string.
         * @param value The value of the string.
         * @throws IllegalArgumentException if the string already exists.
         */
        fun addString(identifier: String, value: String, formatted: Boolean) =
            StringResource(identifier, value, formatted).include()

        /**
         * Add an array to the resources.
         *
         * @param arrayResource The array resource to add.
         */
        fun addArray(arrayResource: ArrayResource) =
            arrayResource.include()

        /**
         * Add a preference fragment to the preferences.
         *
         * @param preference The preference to add.
         */
        fun addRootPreferences(vararg preference: BasePreference) =
            preference.forEach { revancedPreferenceNode!!.addPreference(it) { res -> res.include() } }

        /**
         * Add a new resource to the resources.
         *
         * @throws IllegalArgumentException if the resource already exists.
         */
        private fun IResource.include() {
            when(this) {
                is StringResource -> {
                    if (strings.any { it.name == name }) return
                    strings.add(this)
                }
                is ArrayResource -> {
                    if (arrays.any { it.name == name }) return
                    arrays.add(this)
                }
                else -> throw NotImplementedError("Unsupported resource type")
            }
        }

        private fun DomFileEditor?.getNode(tagName: String) = this!!.file.getElementsByTagName(tagName).item(0)
    }

    override fun close() {
        arrays.forEach { array ->
            arraysNode!!.addResource(array) { it.include() }
        }
        strings.forEach { stringsNode!!.addResource(it) }

        revancedPreferencesEditor?.close()
        stringsEditor?.close()
        arraysEditor?.close()
    }
}