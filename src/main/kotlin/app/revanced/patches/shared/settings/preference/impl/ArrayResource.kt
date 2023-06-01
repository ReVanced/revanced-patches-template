package app.revanced.patches.shared.settings.preference.impl

import app.revanced.arsc.resource.Array
import app.revanced.patches.shared.settings.preference.IResource

// TODO: allow specifying an array resource file instead of using a list of StringResources
/**
 *  An array resource.
 *
 *  @param name The name of the array resource.
 *  @param items The items of the array resource.
 */
internal class ArrayResource(
    override val name: String,
    private val items: List<StringResource>
) : IResource {
    override val patcherValue = Array(items.map { app.revanced.arsc.resource.StringResource(it.value) })
    override val type = "array"
}