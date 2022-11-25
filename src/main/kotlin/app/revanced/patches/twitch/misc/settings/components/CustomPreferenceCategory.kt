package app.revanced.patches.twitch.misc.settings.components

import app.revanced.patches.shared.settings.BasePreference
import app.revanced.patches.shared.settings.impl.PreferenceCategory
import app.revanced.patches.shared.settings.impl.StringResource

/**
 * Customized reference category for Twitch.
 *
 * @param key The key of the preference.
 * @param title The title of the preference.
 * @param preferences Child preferences of this category.
 */
internal open class CustomPreferenceCategory(
    key: String,
    title: StringResource,
    preferences: List<BasePreference>
) : PreferenceCategory(key, title, preferences) {
    override val tag: String = "app.revanced.twitch.settingsmenu.preference.CustomPreferenceCategory"
}