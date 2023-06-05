package app.revanced.patches.shared.settings.preference.impl

import app.revanced.patches.shared.settings.preference.BaseResource
import app.revanced.patches.shared.settings.preference.DefaultBasePreference
import app.revanced.patches.shared.settings.preference.addSummary
import app.revanced.patches.shared.settings.resource.patch.AbstractSettingsResourcePatch.Companion.include
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
    val entries: ArrayResource,
    val entryValues: ArrayResource,
    summaryKey: String? = null,
    default: String? = null,
) : DefaultBasePreference<String>(key, titleKey, summaryKey, "ListPreference", default) {

    @Deprecated("Add strings to strings resource file and used non deprecated keyed constructor")
    constructor(
        key: String,
        title: StringResource,
        entries: ArrayResource,
        entryValues: ArrayResource,
        summary: StringResource? = null,
        default: String? = null
    ) : this(key, title.name, entries, entryValues, summary?.name, default) {
        title.include()
        summary?.include()
    }

    override fun serialize(ownerDocument: Document, resourceCallback: (BaseResource) -> Unit) =
        super.serialize(ownerDocument, resourceCallback).apply {
            setAttribute("android:entries", "@array/${entries.also { resourceCallback.invoke(it) }.name}")
            setAttribute("android:entryValues", "@array/${entryValues.also { resourceCallback.invoke(it) }.name}")
            addSummary(summaryKey)
        }
}