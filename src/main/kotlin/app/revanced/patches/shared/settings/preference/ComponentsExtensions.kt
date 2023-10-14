package app.revanced.patches.shared.settings.preference

import app.revanced.patches.shared.settings.AbstractSettingsResourcePatch.Companion.assertStringExists
import org.w3c.dom.Element
import org.w3c.dom.Node

/**
 * Add a resource node child
 *
 * @param resource The resource to add.
 * @param resourceCallback Called when a resource has been processed.
 */
internal fun Node.addResource(resource: BaseResource, resourceCallback: (BaseResource) -> Unit = { }) {
    appendChild(resource.serialize(ownerDocument, resourceCallback))
}

/**
 * Add a preference node child to the settings.
 *
 * @param preference The preference to add.
 * @param resourceCallback Called when a resource has been processed.
 */
internal fun Node.addPreference(preference: BasePreference, resourceCallback: ((BaseResource) -> Unit) = { }) {
    appendChild(preference.serialize(ownerDocument, resourceCallback))
}

internal fun Element.addSummary(summaryResourceKey: String?, summaryType: SummaryType = SummaryType.DEFAULT)  {
    if (summaryResourceKey != null) {
        setAttribute("android:${summaryType.type}", "@string/${assertStringExists(summaryResourceKey)}")
    }
}

internal fun <T> Element.addDefault(default: T) {
    if (default is Boolean && !(default as Boolean)) return // No need to include the default, as no value already means 'false'
    default?.let {
        setAttribute(
            "android:defaultValue", when (it) {
                is Boolean -> it.toString()
                is String -> it
                else -> throw IllegalArgumentException("Unsupported default value type: ${it::class.java.name}")
            }
        )
    }
}

internal fun CharSequence.removePunctuation(): String {
    val punctuation = "\\p{P}+".toRegex()
    return this.replace(punctuation, "")
}