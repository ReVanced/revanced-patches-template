package app.revanced.patches.youtube.misc.translations

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patches.youtube.misc.settings.SettingsPatch
import app.revanced.util.resources.ResourceUtils

@Patch(
    //name = "Translations", // TODO: release this after translations are usable
    description = "Adds translations to ReVanced.",
    dependencies = [SettingsPatch::class],
    compatiblePackages = [
        CompatiblePackage(
            "com.google.android.youtube"
        )
    ]
)
object TranslationResourcePatch : ResourcePatch() {

    private const val TRANSLATION_RESOURCE_DIRECTORY = "youtube/translation"

    override fun execute(context: ResourceContext) {
        // Look in the jar file and find the paths of the translation string files.
        ResourceUtils.copyLocalizedStringFiles(context, TRANSLATION_RESOURCE_DIRECTORY)
    }
}