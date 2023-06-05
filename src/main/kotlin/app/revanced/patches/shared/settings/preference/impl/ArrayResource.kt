package app.revanced.patches.shared.settings.preference.impl

import app.revanced.patches.shared.settings.preference.BaseResource
import org.w3c.dom.Document
import java.util.stream.Collectors

/**
 *  An array resource.
 *
 *  @param name The name of the array resource.
 *  @param items The items of the array resource.
 */
internal class ArrayResource(
    name: String,
    val items: List<String>,
    val itemsLegacy: List<StringResource>? = null
) : BaseResource(name, "string-array") {

    @Deprecated("Add strings to strings resource file and used non deprecated keyed constructor")
    constructor(
        name: String,
        legacyItems: List<StringResource>
        ) : this(name, legacyItems.stream().map { item -> item.name }.collect(Collectors.toList()), legacyItems) {
    }

    override fun serialize(ownerDocument: Document, resourceCallback: (BaseResource) -> Unit) =
        super.serialize(ownerDocument, resourceCallback).apply {
            setAttribute("name", name)

            itemsLegacy?.forEach { item ->
                resourceCallback.invoke(item)
            }

            items.forEach { item ->
                this.appendChild(ownerDocument.createElement("item").also { itemNode ->
                    itemNode.textContent = "@string/$item"
                })
            }
        }
}