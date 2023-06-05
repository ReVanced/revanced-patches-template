package app.revanced.patches.shared.settings.preference.impl

import app.revanced.patches.shared.settings.preference.BasePreference
import app.revanced.patches.shared.settings.preference.BaseResource
import app.revanced.patches.shared.settings.resource.patch.AbstractSettingsResourcePatch.Companion.include
import org.w3c.dom.Document

/**
 * A preference object.
 *
 * @param key The key of the preference.
 * @param titleKey The title of the preference.
 * @param summaryKey The summary of the text preference.
 * @param intent The intent of the preference.
 */
internal class Preference(
    key: String,
    titleKey: String,
    summaryKey: String,
    val intent: Intent
) : BasePreference(key, titleKey, summaryKey, "Preference") {

    constructor(
        titleKey: String,
        summaryKey: String,
        intent: Intent
    ) : this("", titleKey, summaryKey, intent)

    @Deprecated("Add strings to strings resource file and used non deprecated keyed constructor")
    constructor(
        key: String,
        title: StringResource,
        summary: StringResource,
        intent: Intent
    ) : this(key, title.name, summary.name, intent) {
        title.include()
        summary?.include()
    }
    @Deprecated("Add strings to strings resource file and used non deprecated keyed constructor")
    constructor(
        title: StringResource,
        summary: StringResource,
        intent: Intent
    ) : this("", title, summary, intent)

    override fun serialize(ownerDocument: Document, resourceCallback: (BaseResource) -> Unit) =
        super.serialize(ownerDocument, resourceCallback).apply {
            this.appendChild(ownerDocument.createElement("intent").also { intentNode ->
                intentNode.setAttribute("android:targetPackage", intent.targetPackage)
                intentNode.setAttribute("android:data", intent.data)
                intentNode.setAttribute("android:targetClass", intent.targetClass)
            })
        }

    internal class Intent(
        internal val targetPackage: String,
        internal val data: String,
        internal val targetClass: String
    )
}