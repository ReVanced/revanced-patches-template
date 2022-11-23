package app.revanced.shared.components.settings.impl

import app.revanced.shared.components.settings.BasePreference
import app.revanced.shared.components.settings.IResource
import app.revanced.shared.components.settings.addSummary
import org.w3c.dom.Document
import org.w3c.dom.Element

/**
 * Preference screen.
 *
 * @param key The key of the preference.
 * @param title The title of the preference.
 * @param preferences Child preferences of this screen.
 * @param summary The summary of the text preference.
 */
internal open class PreferenceScreen(
    key: String,
    title: StringResource,
    val preferences: List<BasePreference>,
    var summary: StringResource? = null
) : BasePreference(key, title) {
    override val tag: String = "PreferenceScreen"

    override fun serialize(ownerDocument: Document, resourceCallback: ((IResource) -> Unit)?): Element {
        return super.serialize(ownerDocument, resourceCallback).apply {
            addSummary(summary?.also { resourceCallback?.invoke(it) })

            for (childPreference in preferences) {
                this.appendChild(childPreference.serialize(ownerDocument, resourceCallback))
            }
        }
    }
}