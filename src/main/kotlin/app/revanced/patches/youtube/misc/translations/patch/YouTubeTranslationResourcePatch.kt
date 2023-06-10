package app.revanced.patches.youtube.misc.translations.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.shared.settings.preference.impl.*
import app.revanced.patches.youtube.misc.settings.bytecode.patch.YouTubeSettingsPatch
import app.revanced.patches.youtube.misc.translations.annotation.YouTubeTranslationCompatibility
import app.revanced.util.resources.ResourceUtils

@Patch
@Name("translations")
@Description("Adds translations to YouTube.")
@YouTubeTranslationCompatibility
@DependsOn([YouTubeSettingsPatch::class])
@Version("0.0.1")
class YouTubeTranslationResourcePatch : ResourcePatch {

    override fun execute(context: ResourceContext): PatchResult {
        ResourceUtils.copyLocalizedStringFiles(context, TRANSLATION_RESOURCE_DIRECTORY)

        return PatchResultSuccess()
    }

    private companion object {
        const val TRANSLATION_RESOURCE_DIRECTORY = "youtube/translation"
    }

}