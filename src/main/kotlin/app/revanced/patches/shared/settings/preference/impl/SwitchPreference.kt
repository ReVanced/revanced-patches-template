package app.revanced.patches.shared.settings.preference.impl

import app.revanced.patches.shared.settings.preference.BaseResource
import app.revanced.patches.shared.settings.preference.DefaultBasePreference
import app.revanced.patches.shared.settings.preference.SummaryType
import app.revanced.patches.shared.settings.preference.addSummary
import app.revanced.patches.shared.settings.resource.patch.AbstractSettingsResourcePatch.Companion.include
import org.w3c.dom.Document
import org.w3c.dom.Element

/**
 * A switch preference.
 *
 * @param key The key of the switch.
 * @param titleKey The title of the switch.
 * @param summaryOnKey The summary to show when the preference is enabled.
 * @param summaryOffKey The summary to show when the preference is disabled.
 * @param default The default value of the switch.
 */
internal class SwitchPreference(
    key: String,
    titleKey: String,
    val summaryOnKey: String,
    val summaryOffKey: String,
    default: Boolean = false,
) : DefaultBasePreference<Boolean>( key, titleKey, null, "SwitchPreference", default) {

    @Deprecated("Add strings to strings resource file and used non deprecated keyed constructor")
    constructor(
        key: String, title: StringResource,
        summaryOn: StringResource,
        summaryOff: StringResource,
        userDialogMessage: StringResource? = null,
        default: Boolean = false,
        ) : this(key, title.name, summaryOn.name, summaryOff.name, default) {
        title.include()
        summaryOn.include()
        summaryOff.include()
        userDialogMessage?.include()
    }

    override fun serialize(ownerDocument: Document, resourceCallback: (BaseResource) -> Unit): Element {
        return super.serialize(ownerDocument, resourceCallback).apply {
            addSummary(summaryOnKey, SummaryType.ON)
            addSummary(summaryOffKey, SummaryType.OFF)
        }
    }
}