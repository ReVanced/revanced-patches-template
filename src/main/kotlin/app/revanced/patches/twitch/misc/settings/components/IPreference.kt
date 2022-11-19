package app.revanced.patches.twitch.misc.settings.components

import app.revanced.patches.twitch.misc.settings.components.impl.StringResource

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