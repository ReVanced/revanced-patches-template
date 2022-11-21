package app.revanced.shared.components.settings.impl

/**
 *  Represents an array resource.
 *
 *  @param name The name of the array resource.
 *  @param items The items of the array resource.
 */
internal data class ArrayResource(val name: String, val items: List<StringResource>)