package app.revanced.patches.youtube.misc.settings.framework.components.impl

import app.revanced.patches.youtube.misc.settings.framework.components.BasePreference

/**
 * A Preference object.
 *
 * @param key The key of the preference.
 * @param title The title of the preference.
 * @param preferences Child preferences of this screen.
 * @param summary The summary of the text preference.
 */
internal class Preference(
    key: String,
    title: StringResource,
    val intent: Intent,
    val summary: StringResource? = null
) : BasePreference(key, title) {
    override val tag: String = "Preference"

    data class Intent(val targetPackage: String, val data: String, val targetClass: String)
}