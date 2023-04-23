package app.revanced.patches.shared.settings.preference.impl

import app.revanced.patcher.arsc.Array
import app.revanced.patches.shared.settings.preference.IResource

/**
 *  Represents an array resource.
 *
 *  @param name The name of the array resource.
 *  @param items The items of the array resource.
 */
// TODO: allow specifying an array resource file instead of using a list of StringResources
internal data class ArrayResource(
    override val name: String,
    val items: List<StringResource>
) : IResource {
    override val patcherValue = Array(items.map { app.revanced.patcher.arsc.StringResource(it.value) })
    override val type = "string"
}