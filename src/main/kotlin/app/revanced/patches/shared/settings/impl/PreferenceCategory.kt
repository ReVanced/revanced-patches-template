package app.revanced.patches.shared.settings.impl

import app.revanced.patches.shared.settings.BasePreference
import app.revanced.patches.shared.settings.IResource
import org.w3c.dom.Document
import org.w3c.dom.Element

/**
 * Preference category.
 *
 * @param key The key of the preference.
 * @param title The title of the preference.
 * @param preferences Child preferences of this category.
 */
internal open class PreferenceCategory(
    key: String,
    title: StringResource,
    val preferences: List<BasePreference>
) : BasePreference(key, title) {
    override val tag: String = "PreferenceCategory"

    override fun serialize(ownerDocument: Document, resourceCallback: ((IResource) -> Unit)?): Element {
        return super.serialize(ownerDocument, resourceCallback).apply {
            for (childPreference in preferences) {
                this.appendChild(childPreference.serialize(ownerDocument, resourceCallback))
            }
        }
    }
}