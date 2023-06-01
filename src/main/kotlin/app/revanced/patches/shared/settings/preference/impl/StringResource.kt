package app.revanced.patches.shared.settings.preference.impl

import app.revanced.arsc.resource.StringResource
import app.revanced.patches.shared.settings.preference.IResource

/**
 * A string value.
 * Represets a string in the strings.xml file.
 *
 * @param name The name of the string
 * @param value The value of the string
 */
internal class StringResource(
    override val name: String,
    val value: String
) : IResource {
    override val type = "string"
    override val patcherValue = StringResource(value)
}