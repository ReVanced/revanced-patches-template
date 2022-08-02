package app.revanced.patches.youtube.misc.settings.resource.patch

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.impl.DomFileEditor
import app.revanced.patcher.data.impl.ResourceData
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.Dependencies
import app.revanced.patcher.patch.impl.ResourcePatch
import app.revanced.patches.youtube.misc.manifest.patch.FixLocaleConfigErrorPatch
import app.revanced.patches.youtube.misc.settings.annotations.SettingsCompatibility
import app.revanced.patches.youtube.misc.settings.framework.components.BasePreference
import app.revanced.patches.youtube.misc.settings.framework.components.impl.*
import app.revanced.util.resources.ResourceUtils
import app.revanced.util.resources.ResourceUtils.copyResources
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.io.Closeable

@Name("settings-resource-patch")
@SettingsCompatibility
@Dependencies([FixLocaleConfigErrorPatch::class])
@Version("0.0.1")
class SettingsResourcePatch : ResourcePatch(), Closeable {

    override fun execute(data: ResourceData): PatchResult {
        /*
         * create missing directory for the resources
         */
        data["res/drawable-ldrtl-xxxhdpi"].mkdirs()

        /*
         * copy layout resources
         */
        arrayOf(
            ResourceUtils.ResourceGroup(
                "layout",
                "revanced_settings_toolbar.xml",
                "revanced_settings_with_toolbar.xml",
                "revanced_settings_with_toolbar_layout.xml"
            ),
            ResourceUtils.ResourceGroup(
                "xml", "revanced_prefs.xml" // template for new preferences
            ),
            ResourceUtils.ResourceGroup(
                // required resource for back button, because when the base APK is used, this resource will not exist
                "drawable-xxxhdpi", "quantum_ic_arrow_back_white_24.png"
            ),
            ResourceUtils.ResourceGroup(
                // required resource for back button, because when the base APK is used, this resource will not exist
                "drawable-ldrtl-xxxhdpi", "quantum_ic_arrow_back_white_24.png"
            )
        ).forEach { resourceGroup ->
            data.copyResources("settings", resourceGroup)
        }

        data.xmlEditor["AndroidManifest.xml"].use { editor ->
            editor.file.getElementsByTagName("manifest").item(0).also {
                it.appendChild(it.ownerDocument.createElement("uses-permission").also { element ->
                    element.setAttribute("android:name", "android.permission.SCHEDULE_EXACT_ALARM")
                })
            }
        }

        revancedPreferencesEditor = data.xmlEditor["res/xml/revanced_prefs.xml"]
        preferencesEditor = data.xmlEditor["res/xml/settings_fragment.xml"]

        stringsEditor = data.xmlEditor["res/values/strings.xml"]
        arraysEditor = data.xmlEditor["res/values/arrays.xml"]

        // include the existing string of the from revanced_settings_toolbar.xml
        StringResource("revanced_settings", "ReVanced Settings").include()

        return PatchResultSuccess()
    }


    internal companion object {
        // if this is not null, all intents will be renamed to this
        private var overrideIntentPackage: String? = null

        private var revancedPreferenceNode: Node? = null
        private var preferencesNode: Node? = null

        private var stringsNode: Node? = null
        private var arraysNode: Node? = null

        private var revancedPreferencesEditor: DomFileEditor? = null
            set(value) {
                field = value
                this.revancedPreferenceNode = value.getNode("PreferenceScreen")
            }
        private var preferencesEditor: DomFileEditor? = null
            set(value) {
                field = value
                this.preferencesNode = value.getNode("PreferenceScreen")
            }

        private var stringsEditor: DomFileEditor? = null
            set(value) {
                field = value
                this.stringsNode = value.getNode("resources")
            }
        private var arraysEditor: DomFileEditor? = null
            set(value) {
                field = value
                this.arraysNode = value.getNode("resources")
            }

        /**
         * Add an array to the resources.
         *
         * @param arrayResource The array resource to add.
         */
        fun addArray(arrayResource: ArrayResource) {
            arraysNode!!.appendChild(arraysNode!!.ownerDocument.createElement("string-array").also { arrayNode ->
                arrayResource.items.forEach { item ->
                    item.include()

                    arrayNode.setAttribute("name", item.name)

                    arrayNode.appendChild(
                        arrayNode.ownerDocument.createElement("item").also { itemNode ->
                            itemNode.textContent = item.value
                        }
                    )
                }
            })
        }

        /**
         * Add a preference screen to the settings.
         *
         * @param preferenceScreen The name of the preference screen.
         */
        fun addPreferenceScreen(preferenceScreen: PreferenceScreen) =
            revancedPreferenceNode!!.addPreference(preferenceScreen)

        /**
         * Add a preference fragment to the preferences.
         *
         * @param preference The preference to add.
         */
        fun addPreference(preference: Preference) {
            preferencesNode!!.appendChild(
                preferencesNode.createElement(preference.tag).also { preferenceNode ->
                    preferenceNode.setAttribute("android:title", "@string/${preference.title.also { it.include() }.name}")
                    preference.summary?.let { summary ->
                        preferenceNode.setAttribute("android:summary", summary.also { it.include() }.name)
                    }

                    preferenceNode.appendChild(preferenceNode.createElement("intent").also { intentNode ->
                        intentNode.setAttribute("android:targetPackage", preference.intent.targetPackage)
                        intentNode.setAttribute("android:data", preference.intent.data)
                        intentNode.setAttribute("android:targetClass", preference.intent.targetClass)
                    })
                }
            )
        }


        /**
         * Add a preference to the settings.
         *
         * @param preference The preference to add.
         */
        private fun Node.addPreference(preference: BasePreference) {
            // add a summary to the element
            fun Element.addSummary(summaryResource: StringResource?, summaryType: SummaryType = SummaryType.DEFAULT) =
                summaryResource?.let { summary ->
                    setAttribute("android:${summaryType.type}", "@string/${summary.also { it.include() }.name}")
                }

            fun <T> Element.addDefault(default: T) {
                default?.let {
                    setAttribute(
                        "android:defaultValue", when (it) {
                            is Boolean -> if (it) "true" else "false"
                            is String -> it
                            else -> throw IllegalArgumentException("Unsupported default value type: ${it::class.java.name}")
                        }
                    )
                }
            }

            val preferenceElement = ownerDocument.createElement(preference.tag)
            preferenceElement.setAttribute("android:key", preference.key)
            preferenceElement.setAttribute("android:title", "@string/${preference.title.also { it.include() }.name}")

            when (preference) {
                is PreferenceScreen -> {
                    for (childPreference in preference.preferences) preferenceElement.addPreference(childPreference)
                    preferenceElement.addSummary(preference.summary)
                }
                is SwitchPreference -> {
                    preferenceElement.addDefault(preference.default)
                    preferenceElement.addSummary(preference.summaryOn, SummaryType.ON)
                    preferenceElement.addSummary(preference.summaryOff, SummaryType.OFF)
                }
                is TextPreference -> {
                    preferenceElement.setAttribute("android:inputType", preference.inputType.type)
                    preferenceElement.addDefault(preference.default)
                    preferenceElement.addSummary(preference.summary)
                }
            }

            appendChild(preferenceElement)
        }

        /**
         * Add a new string to the resources.
         *
         * @throws IllegalArgumentException if the string already exists.
         */
        private fun StringResource.include() {
            val strings = stringsNode!!.childNodes
            for (i in 1 until strings.length) {
                val stringNode = strings.item(i)

                if (!stringNode.hasAttributes()) continue
                // check if the string already exists, if so, reuse it
                if (stringNode.attributes.getNamedItem("name").nodeValue == name) return
            }

            stringsNode!!.appendChild(
                stringsNode!!.ownerDocument.createElement("string").also { stringElement ->
                    stringElement.setAttribute("name", name)

                    stringElement.textContent = value
                }
            )
        }

        private fun DomFileEditor?.getNode(tagName: String) =
            this!!.file.getElementsByTagName(tagName).item(0)

        private fun Node?.createElement(tagName: String) = this!!.ownerDocument.createElement(tagName)

        private enum class SummaryType(val type: String) {
            DEFAULT("summary"), ON("summaryOn"), OFF("summaryOff")
        }
    }

    override fun close() {
        // rename the intent package names if it was set
        overrideIntentPackage?.let { packageName ->
            val preferences = preferencesEditor!!.getNode("PreferenceScreen").childNodes
            for (i in 0 until preferences.length)
                (preferences.item(i).firstChild as Element)
                    .setAttribute("android:targetPackage", packageName)
        }

        revancedPreferencesEditor?.close()
        preferencesEditor?.close()
        stringsEditor?.close()
        arraysEditor?.close()
    }
}