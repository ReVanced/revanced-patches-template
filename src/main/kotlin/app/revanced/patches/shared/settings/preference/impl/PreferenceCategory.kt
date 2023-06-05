package app.revanced.patches.shared.settings.preference.impl

import app.revanced.patches.shared.settings.preference.BasePreference
import app.revanced.patches.shared.settings.preference.BaseResource
import app.revanced.patches.shared.settings.resource.patch.AbstractSettingsResourcePatch.Companion.include
import org.w3c.dom.Document

/**
 * A preference category.
 *
 * @param key The key of the preference.
 * @param titleKey The title of the preference.
 * @param preferences Child preferences of this category.
 */
internal open class PreferenceCategory(
    key: String,
    titleKey: String,
    var preferences: List<BasePreference>,
    tag: String = "PreferenceCategory"
) : BasePreference(key, titleKey, null, tag) {

    @Deprecated("Add strings to strings resource file and used non deprecated keyed constructor")
    constructor(
        key: String,
        title: StringResource,
        preferences: List<BasePreference>,
        tag: String = "PreferenceCategory"
    ) : this(key, title.name, preferences, tag) {
        title.include()
    }

    override fun serialize(ownerDocument: Document, resourceCallback: (BaseResource) -> Unit) =
        super.serialize(ownerDocument, resourceCallback).apply {
            for (childPreference in preferences) {
                this.appendChild(childPreference.serialize(ownerDocument, resourceCallback))
            }
        }
}