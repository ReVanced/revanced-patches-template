package app.revanced.patches.shared.settings.preference.impl

import app.revanced.patches.shared.settings.preference.BasePreference
import app.revanced.patches.shared.settings.preference.BaseResource
import app.revanced.patches.shared.settings.preference.addSummary
import app.revanced.patches.shared.settings.resource.patch.AbstractSettingsResourcePatch.Companion.include
import org.w3c.dom.Document

/**
 * A preference screen.
 *
 * @param key The key of the preference.
 * @param titleKey The title of the preference.
 * @param preferences Child preferences of this screen.
 * @param summaryKey The summary of the text preference.
 */
internal open class PreferenceScreen(
    key: String,
    titleKey: String,
    var preferences: List<BasePreference>,
    summaryKey: String? = null
) : BasePreference(key, titleKey, summaryKey, "PreferenceScreen") {

    @Deprecated("Add strings to strings resource file and used non deprecated keyed constructor")
    constructor(
        key: String,
        title: StringResource,
        preferences: List<BasePreference>,
        summary: StringResource? = null
    ) : this(key, title.name, preferences, summary?.name) {
        title.include()
        summary?.include()
    }

    override fun serialize(ownerDocument: Document, resourceCallback: (BaseResource) -> Unit) =
        super.serialize(ownerDocument, resourceCallback).apply {
            addSummary(summaryKey)

            for (childPreference in preferences)
                this.appendChild(childPreference.serialize(ownerDocument, resourceCallback))
        }
}