package app.revanced.patches.shared.settings.preference.impl

import app.revanced.patches.shared.settings.preference.BaseResource
import app.revanced.patches.shared.settings.preference.DefaultBasePreference
import app.revanced.patches.shared.settings.resource.patch.AbstractSettingsResourcePatch.Companion.include
import org.w3c.dom.Document

/**
 * A text preference.
 *
 * @param key The key of the text preference.
 * @param titleKey The title of the text preference.
 * @param inputType The input type of the text preference.
 * @param summaryKey The summary of the text preference.
 * @param default The default value of the text preference.
 */
internal class TextPreference(
    key: String?,
    titleKey: String,
    summaryKey: String?,
    val inputType: InputType = InputType.TEXT,
    default: String? = null,
    tag: String = "app.revanced.integrations.settingsmenu.ResettableEditTextPreference"
) : DefaultBasePreference<String>(key, titleKey, summaryKey, tag, default) {

    @Deprecated("Add strings to strings resource file and used non deprecated keyed constructor")
    constructor(
        key: String?,
        title: StringResource,
        summary: StringResource?,
        inputType: InputType = InputType.TEXT,
        default: String? = null,
        tag: String = "app.revanced.integrations.settingsmenu.ResettableEditTextPreference"
    ) : this(key, title.name, summary?.name, inputType, default, tag) {
        title.include()
        summary?.include()
    }

    override fun serialize(ownerDocument: Document, resourceCallback: (BaseResource) -> Unit) =
        super.serialize(ownerDocument, resourceCallback).apply {
            setAttribute("android:inputType", inputType.type)
        }
}