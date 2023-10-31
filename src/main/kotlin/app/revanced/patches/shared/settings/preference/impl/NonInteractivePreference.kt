package app.revanced.patches.shared.settings.preference.impl

import app.revanced.patches.shared.settings.preference.BasePreference
import app.revanced.patches.shared.settings.preference.addSummary
import org.w3c.dom.Document
import org.w3c.dom.Element

/**
 * A non interactive preference.
 *
 * Not backed by any preference key/value,
 * and cannot be changed by or interacted with by the user.
 *
 * @param titleKey The title of the preference.
 * @param summaryKey The summary of the text preference.
 */
class NonInteractivePreference(
    titleKey: String,
    summaryKey: String,
) : BasePreference(null, titleKey, summaryKey, "Preference") {
    override fun serialize(ownerDocument: Document): Element {
        return super.serialize(ownerDocument).apply {
            addSummary(summaryKey?.also {
                setAttribute("android:selectable", false.toString())
            })
        }
    }
}
