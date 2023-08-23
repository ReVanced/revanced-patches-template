
package app.revanced.patches.twitch.misc.settings.resource.patch

import app.revanced.patcher.data.ResourceContext
import app.revanced.patches.shared.settings.preference.impl.ArrayResource
import app.revanced.patches.shared.settings.preference.impl.PreferenceScreen
import app.revanced.patches.shared.settings.resource.patch.AbstractSettingsResourcePatch
import app.revanced.util.resources.ResourceUtils.mergeStrings

class TwitchSettingsResourcePatch : AbstractSettingsResourcePatch(
    "revanced_prefs",
    "twitch/settings"
) {

    override fun execute(context: ResourceContext) {
        super.execute(context)

        context.mergeStrings("twitch/settings/host/values/strings.xml")
    }

    internal companion object {
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
        fun addPreferenceScreen(preferenceScreen: PreferenceScreen) = AbstractSettingsResourcePatch.addPreference(preferenceScreen)
    }
}