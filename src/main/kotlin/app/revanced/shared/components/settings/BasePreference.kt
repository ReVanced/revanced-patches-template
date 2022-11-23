package app.revanced.shared.components.settings

import app.revanced.shared.components.settings.impl.StringResource
import org.w3c.dom.Document
import org.w3c.dom.Element

/**
 * Base preference class for all preferences.
 *
 * @param key The key of the preference.
 * @param title The title of the preference.
 */
internal abstract class BasePreference(
    override val key: String,
    override val title: StringResource,
) : IPreference {

    /**
     * Serialize preference element to XML.
     * Overriding methods should invoke super and operate on its return value.
     * @param ownerDocument Target document to create elements from.
     * @param resourceCallback Called when a resource has been processed.
     */
    open fun serialize(ownerDocument: Document, resourceCallback: ((IResource) -> Unit)? = null): Element {
        val preferenceElement = ownerDocument.createElement(tag)
        if(key.isNotEmpty())
            preferenceElement.setAttribute("android:key", key)
        preferenceElement.setAttribute("android:title", "@string/${title.also { resourceCallback?.invoke(it) }.name}")
        return preferenceElement
    }
}