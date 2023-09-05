package app.revanced.patches.twitch.misc.translations.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patches.shared.settings.preference.impl.*
import app.revanced.patches.twitch.misc.settings.resource.patch.SettingsResourcePatch
import app.revanced.patches.twitch.misc.translations.annotation.TranslationCompatibility
import app.revanced.util.resources.ResourceUtils

//@Patch // TODO: release this after translations are usable
@Name("Translations")
@Description("Adds translations to ReVanced.")
@TranslationCompatibility
@DependsOn([SettingsResourcePatch::class])
class TranslationResourcePatch : ResourcePatch {

    override fun execute(context: ResourceContext) {
        // Look in the jar file and find the paths of the translation string files.
        ResourceUtils.copyLocalizedStringFiles(context, TRANSLATION_RESOURCE_DIRECTORY)
    }

    private companion object {
        const val TRANSLATION_RESOURCE_DIRECTORY = "twitch/translation"
    }

}