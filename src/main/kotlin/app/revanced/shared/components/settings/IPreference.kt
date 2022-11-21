package app.revanced.shared.components.settings

import app.revanced.shared.components.settings.impl.StringResource

/**
 * Preference
 */
internal interface IPreference {
    /**
     * Key of the preference.
     */
    val key: String

    /**
     * Title of the preference.
     */
    val title: StringResource

    /**
     * Tag name of the preference.
     */
    val tag: String
}