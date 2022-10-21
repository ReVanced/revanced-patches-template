package app.revanced.patches.youtube.misc.settings.framework.components.impl

import app.revanced.patches.youtube.misc.settings.framework.components.BasePreference

internal class ListPreference(
    key : String,
    title : StringResource,
    val summary : StringResource
): BasePreference(key, title) {
    override val tag: String = "ListPreference"
}