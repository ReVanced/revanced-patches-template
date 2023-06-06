
package app.revanced.patches.twitch.misc.settings.resource.patch
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patches.shared.settings.preference.impl.ArrayResource
import app.revanced.patches.shared.settings.preference.impl.PreferenceScreen
import app.revanced.patches.shared.settings.resource.patch.AbstractSettingsResourcePatch
import app.revanced.patches.twitch.misc.integrations.patch.IntegrationsPatch
import app.revanced.patches.twitch.misc.settings.annotations.SettingsCompatibility
import app.revanced.util.resources.ResourceUtils.mergeStrings

@Name("settings-resource-patch")
@DependsOn([IntegrationsPatch::class])
@SettingsCompatibility
@Version("0.0.1")
class TwitchSettingsResourcePatch : AbstractSettingsResourcePatch(
    "revanced_prefs",
    "twitch/settings"
) {

    override fun execute(context: ResourceContext): PatchResult {
        super.execute(context)

        context.mergeStrings("twitch/settings/host/values/strings.xml")

        return PatchResultSuccess()
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
