package app.revanced.patches.tiktok.misc.translations.patch

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
import app.revanced.patches.tiktok.misc.settings.bytecode.patch.TikTokSettingsPatch
import app.revanced.patches.tiktok.misc.translations.annotation.TikTokTranslationCompatibility
import app.revanced.util.resources.ResourceUtils

@Patch
@Name("translations")
@Description("Adds translations to TikTok.")
@TikTokTranslationCompatibility
@DependsOn([TikTokSettingsPatch::class])
@Version("0.0.1")
class TikTokTranslationResourcePatch : ResourcePatch {

    override fun execute(context: ResourceContext): PatchResult {
        ResourceUtils.copyLocalizedStringFiles(context, TRANSLATION_RESOURCE_DIRECTORY)

        return PatchResultSuccess()
    }

    private companion object {
        const val TRANSLATION_RESOURCE_DIRECTORY = "tiktok/translation"
    }

}