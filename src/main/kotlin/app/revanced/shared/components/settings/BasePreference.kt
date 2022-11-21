package app.revanced.shared.components.settings

import app.revanced.shared.components.settings.impl.StringResource

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