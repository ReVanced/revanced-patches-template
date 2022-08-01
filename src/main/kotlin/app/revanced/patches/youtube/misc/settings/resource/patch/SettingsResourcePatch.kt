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
         * Copy layout resources
         */
        arrayOf(
            ResourceUtils.ResourceGroup(
                "layout",
                "revanced_settings_toolbar.xml",
                "revanced_settings_with_toolbar.xml",
                "revanced_settings_with_toolbar_layout.xml"
            ), ResourceUtils.ResourceGroup(
                "xml", "revanced_prefs.xml" // template for new preferences
            )
        ).forEach { resourceGroup ->
            data.copyResources("settings", resourceGroup)
        }

        data.xmlEditor["AndroidManifest.xml"].use {
            val manifestNode = it.file.getElementsByTagName("manifest").item(0)

            val element = it.file.createElement("uses-permission")
            element.setAttribute("android:name", "android.permission.SCHEDULE_EXACT_ALARM")
            manifestNode.appendChild(element)
        }

        reVancedPreferencesEditor = data.xmlEditor["res/xml/revanced_prefs.xml"]
        preferencesEditor = data.xmlEditor["res/values/arrays.xml"]

        stringsEditor = data.xmlEditor["res/values/strings.xml"]
        arraysEditor = data.xmlEditor["res/values/arrays.xml"]

        return PatchResultSuccess()
    }


    internal companion object {
        // If this is not null, all intents will be renamed to this
        private var overrideIntentPackage: String? = null

        private var reVancedPreferenceNode: Node? = null
        private var preferencesNode: Node? = null

        private var stringsNode: Node? = null
        private var arraysNode: Node? = null

        private var reVancedPreferencesEditor: DomFileEditor? = null
            set(value) {
                field = value
                this.reVancedPreferenceNode = value.getNode("PreferenceScreen")
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

        private val stringResources: MutableList<String> = mutableListOf()

        /**
         * Add an array to the resources.
         *
         * @param arrayResource The array resource to add.
         */
        fun addArray(arrayResource: ArrayResource) {
            arraysNode!!.appendChild(arraysNode.createElement("string-array").also { arrayNode ->
                arrayResource.items.forEach { item ->
                    item.include()

                    arrayNode.setAttribute("name", item.name)

                    arraysNode.createElement("item").also { itemNode ->
                        itemNode.textContent = item.value
                        arrayNode.appendChild(itemNode)
                    }
                }
            })
        }

        /**
         * Add a preference screen to the settings.
         *
         * @param preferenceScreen The name of the preference screen.
         */
        fun addPreferenceScreen(preferenceScreen: PreferenceScreen) {
            reVancedPreferencesEditor!!.use {
                reVancedPreferenceNode!!.addPreference(preferenceScreen)
            }
        }

        /**
         * Add a preference fragment to the preferences.
         *
         * @param preference The preference to add.
         */
        fun addPreference(preference: Preference) {
            preferencesNode!!.appendChild(
                preferencesNode.createElement(preference.tag).also { preferenceNode ->
                    preferenceNode.setAttribute("android:title", preference.title.also { it.include() }.name)
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
            // Add a summary to the element
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
                    for (childPreference in preference.preferences) addPreference(childPreference)
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
        private fun StringResource.include() = if (stringResources.contains(name)) {
            throw IllegalArgumentException("String resource with the same name already exists: $name")
        } else {
            stringsNode!!.appendChild(
                stringsNode!!.ownerDocument.createElement("string").also { stringElement ->
                    stringElement.setAttribute("name", name)
                    stringElement.textContent = value
                }
            )

            stringResources.add(name)
        }

        private fun DomFileEditor?.getNode(tagName: String) =
            this!!.file.getElementsByTagName(tagName).item(0)

        private fun Node?.createElement(tagName: String) = this!!.ownerDocument.createElement(tagName)

        private enum class SummaryType(val type: String) {
            DEFAULT("summary"), ON("summaryOn"), OFF("summaryOff")
        }
    }

    override fun close() {
        // Rename the intent package names if it was set
        overrideIntentPackage?.let { packageName ->
            val preferences = preferencesEditor!!.getNode("PreferenceScreen").childNodes
            for (i in 0 until preferences.length)
                (preferences.item(i).firstChild as Element)
                    .setAttribute("android:targetPackage", packageName)
        }

        reVancedPreferencesEditor?.close()
        preferencesEditor?.close()
        stringsEditor?.close()
        arraysEditor?.close()
    }
}