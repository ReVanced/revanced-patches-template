package app.revanced.patches.youtube.misc.translations.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patches.shared.settings.preference.impl.*
import app.revanced.patches.youtube.misc.settings.bytecode.patch.YouTubeSettingsPatch
import app.revanced.patches.youtube.misc.translations.annotation.YouTubeTranslationCompatibility
import app.revanced.util.resources.ResourceUtils

//@Patch // TODO: release this after translations are usable
@Name("Translations")
@Description("Adds translations to ReVanced.")
@YouTubeTranslationCompatibility
@DependsOn([YouTubeSettingsPatch::class])
class YouTubeTranslationResourcePatch : ResourcePatch {

    override fun execute(context: ResourceContext) {
        // Look in the jar file and find the paths of the translation string files.
        ResourceUtils.copyLocalizedStringFiles(context, TRANSLATION_RESOURCE_DIRECTORY)
    }

    private companion object {
        const val TRANSLATION_RESOURCE_DIRECTORY = "youtube/translation"
    }

}