package app.revanced.shared.components.settings.impl

import app.revanced.shared.components.settings.BasePreference
import app.revanced.shared.components.settings.IResource
import app.revanced.shared.components.settings.addDefault
import org.w3c.dom.Document
import org.w3c.dom.Element

/**
 * List preference.
 *
 * @param key The key of the list preference.
 * @param title The title of the list preference.
 * @param entries The human-readable entries of the list preference.
 * @param entryValues The entry values of the list preference.
 * @param default The default entry value of the list preference.
 */
internal class ListPreference(
    key: String,
    title: StringResource,
    var entries: ArrayResource,
    var entryValues: ArrayResource,
    var default: String? = null
) : BasePreference(key, title) {
    override val tag: String = "ListPreference"

    override fun serialize(ownerDocument: Document, resourceCallback: ((IResource) -> Unit)?): Element {
        return super.serialize(ownerDocument, resourceCallback).apply {
            setAttribute("android:entries", "@array/${entries.also { resourceCallback?.invoke(it) }.name}")
            setAttribute("android:entryValues", "@array/${entryValues.also { resourceCallback?.invoke(it) }.name}")
            addDefault(default)
        }
    }
}