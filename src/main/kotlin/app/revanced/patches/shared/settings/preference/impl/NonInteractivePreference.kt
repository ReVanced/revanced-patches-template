package app.revanced.patches.shared.settings.preference.impl

import app.revanced.patches.shared.settings.preference.BasePreference
import app.revanced.patches.shared.settings.preference.BaseResource
import app.revanced.patches.shared.settings.preference.addSummary
import app.revanced.patches.shared.settings.resource.patch.AbstractSettingsResourcePatch.Companion.include
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
internal class NonInteractivePreference(
    titleKey: String,
    summaryKey: String,
) : BasePreference(null, titleKey, summaryKey, "Preference") {

    @Deprecated("Add strings to strings resource file and used non deprecated keyed constructor")
    constructor(
        title: StringResource,
        summary: StringResource,
    ) : this(title.name, summary.name) {
        title.include()
        summary?.include()
    }

    override fun serialize(ownerDocument: Document, resourceCallback: (BaseResource) -> Unit): Element {
        return super.serialize(ownerDocument, resourceCallback).apply {
            addSummary(summaryKey?.also {
                setAttribute("android:selectable", false.toString())
            })
        }
    }
}
