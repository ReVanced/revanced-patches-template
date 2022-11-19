package app.revanced.patches.twitch.misc.settings.components.impl

import app.revanced.patches.twitch.misc.settings.components.BasePreference

/**
 * Preference category.
 *
 * @param key The key of the preference.
 * @param title The title of the preference.
 * @param preferences Child preferences of this category.
 * @param summary The summary of the text preference.
 */
internal open class PreferenceCategory(
    key: String,
    title: StringResource,
    val preferences: List<BasePreference>
) : BasePreference(key, title) {
    override val tag: String = "app.revanced.twitch.settingsmenu.preference.CustomPreferenceCategory"
}