package app.revanced.patches.twitch.misc.settings.components.impl

import app.revanced.patches.twitch.misc.settings.components.BasePreference

/**
 * List preference.
 *
 * @param key The key of the list preference.
 * @param title The title of the list preference.
 * @param entries The human-readable entries of the list preference.
 * @param entryValues The entry values of the list preference.
 * @param default The default entry value of the list preference.
 */
internal class ListPreference(
    key: String,
    title: StringResource,
    var entries: ArrayResource,
    var entryValues: ArrayResource,
    var default: String? = null
) : BasePreference(key, title) {
    override val tag: String = "ListPreference"
}