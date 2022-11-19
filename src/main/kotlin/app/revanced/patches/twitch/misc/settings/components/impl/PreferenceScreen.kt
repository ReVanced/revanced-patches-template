package app.revanced.patches.twitch.misc.settings.components.impl

import app.revanced.patches.twitch.misc.settings.components.BasePreference

/**
 * Preference screen.
 *
 * @param key The key of the preference.
 * @param title The title of the preference.
 * @param preferences Child preferences of this screen.
 * @param summary The summary of the text preference.
 */
internal open class PreferenceScreen(
    key: String,
    title: StringResource,
    val preferences: MutableList<BasePreference>,
    var summary: StringResource? = null
) : BasePreference(key, title) {
    override val tag: String = "PreferenceScreen"
}