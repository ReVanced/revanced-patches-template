package app.revanced.patches.youtube.misc.translations.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultError
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.shared.settings.preference.impl.*
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch
import app.revanced.util.resources.ResourceUtils
import app.revanced.util.resources.ResourceUtils.copyResources
import java.util.jar.JarFile
import java.util.regex.Pattern

@Patch
@Name("translations")
@Description("Adds translations to YouTube.")
@DependsOn([SettingsPatch::class])
@Version("0.0.1")
class TranslationResourcePatch : ResourcePatch {

    override fun execute(context: ResourceContext): PatchResult {
        // Look in the jar file and find the paths of the translation string files.
        val pattern = Pattern.compile("$TRANSLATION_RESOURCE_DIRECTORY/([-_a-zA-Z0-9]+)/strings\\.xml")

        var jf: JarFile? = null
        try {
            jf = JarFile(this.javaClass.protectionDomain.codeSource.location.toURI().path)
            val entries = jf.entries()
            var foundElements = false
            while (entries.hasMoreElements()) {
                val match = pattern.matcher(entries.nextElement().name)
                if (match.find()) {
                    val languageDirectory = match.group(1)
                    context.copyResources(TRANSLATION_RESOURCE_DIRECTORY,
                        ResourceUtils.ResourceGroup(languageDirectory, "strings.xml")
                    )
                    foundElements = true
                }
            }
            if (!foundElements) return PatchResultError("could not find translated string files")
        } finally {
            jf?.close()
        }

        return PatchResultSuccess()
    }

    private companion object {
        const val TRANSLATION_RESOURCE_DIRECTORY = "youtube/translation"
    }

}