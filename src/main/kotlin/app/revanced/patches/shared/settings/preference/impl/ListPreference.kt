package app.revanced.patches.shared.settings.preference.impl

import app.revanced.patches.shared.settings.preference.BaseResource
import app.revanced.patches.shared.settings.preference.DefaultBasePreference
import app.revanced.patches.shared.settings.preference.addSummary
import org.w3c.dom.Document

/**
 * List preference.
 *
 * @param key The key of the list preference.
 * @param titleKey The title of the list preference.
 * @param entries The human-readable entries of the list preference.
 * @param entryValues The entry values of the list preference.
 * @param summaryKey The summary of the list preference.
 * @param default The default entry value of the list preference.
 */
internal class ListPreference(
    key: String,
    titleKey: String,
    val entries: ArrayResource?,
    val entryValues: ArrayResource?,
    summaryKey: String? = null,
    default: String? = null,
) : DefaultBasePreference<String>(key, titleKey, summaryKey, "ListPreference", default) {

    override fun serialize(ownerDocument: Document, resourceCallback: (BaseResource) -> Unit) =
        super.serialize(ownerDocument, resourceCallback).apply {
            if (entries != null) {
                setAttribute("android:entries", "@array/${entries.also { resourceCallback.invoke(it) }.name}")
            }
            if (entryValues != null) {
                setAttribute("android:entryValues", "@array/${entryValues.also { resourceCallback.invoke(it) }.name}")
            }
            addSummary(summaryKey)
        }
}