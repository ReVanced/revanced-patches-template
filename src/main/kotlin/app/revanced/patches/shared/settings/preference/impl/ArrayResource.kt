package app.revanced.patches.shared.settings.preference.impl

import app.revanced.patches.shared.settings.AbstractSettingsResourcePatch.Companion.validateStringIsMerged
import app.revanced.patches.shared.settings.preference.BaseResource
import org.w3c.dom.Document

/**
 *  An array resource.
 *
 *  @param name The name of the array resource.
 *  @param items The items of the array resource.
 *  @param literalValues If the values are to be used exactly as is.
 *                       If false, the values are treated as Strings.xml entries.
 */
class ArrayResource(
    name: String,
    val items: Iterable<String>,
    val literalValues : Boolean = false
) : BaseResource(name, "string-array") {

    override fun serialize(ownerDocument: Document, resourceCallback: (BaseResource) -> Unit) =
        super.serialize(ownerDocument, resourceCallback).apply {
            setAttribute("name", name)

            items.forEach { item ->
                this.appendChild(ownerDocument.createElement("item").also { itemNode ->
                    itemNode.textContent = if (literalValues) item else "@string/${validateStringIsMerged(item)}"
                })
            }
        }
}