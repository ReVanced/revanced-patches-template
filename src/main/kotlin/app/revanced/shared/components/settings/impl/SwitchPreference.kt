package app.revanced.shared.components.settings.impl

import app.revanced.shared.components.settings.*
import app.revanced.shared.components.settings.BasePreference
import app.revanced.shared.components.settings.IResource
import app.revanced.shared.components.settings.addDefault
import app.revanced.shared.components.settings.addSummary
import org.w3c.dom.Document
import org.w3c.dom.Element

/**
 * Switch preference.
 *
 * @param key The key of the switch.
 * @param title The title of the switch.
 * @param default The default value of the switch.
 * @param summaryOn The summary to show when the preference is enabled.
 * @param summaryOff The summary to show when the preference is disabled.
 */
internal class SwitchPreference(
    key: String, title: StringResource,
    val default: Boolean = false,
    var summaryOn: StringResource? = null,
    var summaryOff: StringResource? = null
) : BasePreference(key, title) {
    override val tag: String = "SwitchPreference"

    override fun serialize(ownerDocument: Document, resourceCallback: ((IResource) -> Unit)?): Element {
        return super.serialize(ownerDocument, resourceCallback).apply {
            addDefault(default)
            addSummary(summaryOn?.also { resourceCallback?.invoke(it) }, SummaryType.ON)
            addSummary(summaryOff?.also { resourceCallback?.invoke(it) }, SummaryType.OFF)
        }
    }
}