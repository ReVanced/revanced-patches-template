package app.revanced.patches.youtube.misc.settings.framework.components.impl

import app.revanced.patches.youtube.misc.settings.framework.components.BasePreference

internal class ListPreference(
    key : String,
    title : StringResource,
    var entries : ArrayResource,
    var entryValues : ArrayResource,
    var summary : StringResource
): BasePreference(key, title) {
    override val tag: String = "ListPreference"
}
