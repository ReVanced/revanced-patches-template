package app.revanced.patches.twitch.misc.settings.components

import app.revanced.patches.twitch.misc.settings.components.impl.StringResource

/**
 * Base preference class for all preferences.
 *
 * @param key The key of the preference.
 * @param title The title of the preference.
 */
internal abstract class BasePreference(
    override val key: String,
    override val title: StringResource,
) : IPreference