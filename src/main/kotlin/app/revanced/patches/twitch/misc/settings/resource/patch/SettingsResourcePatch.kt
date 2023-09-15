
package app.revanced.patches.twitch.misc.settings.resource.patch

import app.revanced.patcher.data.ResourceContext
import app.revanced.patches.shared.settings.preference.impl.ArrayResource
import app.revanced.patches.shared.settings.preference.impl.PreferenceScreen
import app.revanced.patches.shared.settings.resource.patch.AbstractSettingsResourcePatch
import app.revanced.patches.youtube.misc.settings.resource.patch.SettingsResourcePatch.Companion.mergePatchStrings
import app.revanced.util.resources.ResourceUtils.mergeStrings

class SettingsResourcePatch : AbstractSettingsResourcePatch(
    "revanced_prefs",
    "twitch/settings"
) {

    override fun execute(context: ResourceContext) {
        super.execute(context)

        resourceContext = context
    }

    internal companion object {
        /**
         * Used to merge the strings in [mergePatchStrings].
         */
        private lateinit var resourceContext : ResourceContext

        /* Companion delegates */

        /**
         * Add an array to the resources.
         *
         * @param arrayResource The array resource to add.
         */
        fun addArray(arrayResource: ArrayResource) = AbstractSettingsResourcePatch.addArray(arrayResource)

        /**
         * Add a preference to the settings.
         *
         * @param preferenceScreen The name of the preference screen.
         */
        fun addPreferenceScreen(preferenceScreen: PreferenceScreen) = addPreference(preferenceScreen)

        /**
         * Merge the English strings for a given patch.
         *
         * @param patchName Name of the patch strings xml file.
         */
        fun mergePatchStrings(patchName: String)  {
            resourceContext.mergeStrings("twitch/settings/host/values/$patchName.xml")
        }
    }
}