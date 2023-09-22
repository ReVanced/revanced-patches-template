package app.revanced.patches.twitch.misc.translations

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patches.twitch.misc.integrations.IntegrationsPatch
import app.revanced.patches.twitch.misc.settings.SettingsResourcePatch
import app.revanced.util.resources.ResourceUtils

@Patch(
    //name = "Translations", // TODO: release this after translations are usable
    description = "Adds translations to ReVanced patches.",
    dependencies = [IntegrationsPatch::class, SettingsResourcePatch::class],
    compatiblePackages = [
        CompatiblePackage("tv.twitch.android.app")
    ]
)

object TranslationResourcePatch : ResourcePatch() {

    private const val TRANSLATION_RESOURCE_DIRECTORY = "twitch/translation"

    override fun execute(context: ResourceContext) {
        // Look in the jar file and find the paths of the translation string files.
        ResourceUtils.copyLocalizedStringFiles(context, TRANSLATION_RESOURCE_DIRECTORY)
    }

}