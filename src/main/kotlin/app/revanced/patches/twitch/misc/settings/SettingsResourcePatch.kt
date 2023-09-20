package app.revanced.patches.twitch.misc.settings

import app.revanced.patcher.data.ResourceContext
import app.revanced.patches.shared.settings.AbstractSettingsResourcePatch
import app.revanced.util.resources.ResourceUtils.mergeStrings

object SettingsResourcePatch : AbstractSettingsResourcePatch(
"revanced_prefs",
"twitch/settings"
) {

    /**
     * Used to merge the strings in [mergePatchStrings].
     */
    private lateinit var resourceContext : ResourceContext

    override fun execute(context: ResourceContext) {
        super.execute(context)

        resourceContext = context
    }

    /**
     * Merge the English strings for a given patch.
     *
     * @param patchName Name of the patch strings xml file.
     */
    fun mergePatchStrings(patchName: String)  {
        resourceContext.mergeStrings("twitch/settings/host/values/$patchName.xml")
    }
}