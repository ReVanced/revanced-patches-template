package app.revanced.patches.youtube.layout.branding.header.patch

import app.revanced.patcher.ResourceContext
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version

import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.youtube.layout.branding.header.annotations.PremiumHeadingCompatibility
import app.revanced.patches.youtube.misc.manifest.patch.FixLocaleConfigErrorPatch
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import kotlin.io.path.exists

@Patch
@DependsOn([FixLocaleConfigErrorPatch::class])
@Name("premium-heading")
@Description("Shows premium branding on the home screen.")
@PremiumHeadingCompatibility
@Version("0.0.1")
class PremiumHeadingPatch : ResourcePatch {
    override fun execute(context: ResourceContext): PatchResult {
        val resDirectory = context.getFileOr("res/drawable-hdpi").parentFile

        if (!resDirectory.isDirectory) return PatchResult.Error("The res folder can not be found.")

        val (original, replacement) = "yt_premium_wordmark_header" to "yt_wordmark_header"
        val modes = arrayOf("light", "dark")

        arrayOf("xxxhdpi", "xxhdpi", "xhdpi", "hdpi", "mdpi").forEach { size ->
            val headingDirectory = resDirectory.resolve("drawable-$size")
            modes.forEach { mode ->
                val fromPath = headingDirectory.resolve("${original}_$mode.png").toPath()
                val toPath = headingDirectory.resolve("${replacement}_$mode.png").toPath()

                if (!fromPath.exists())
                    return PatchResult.Error("The file $fromPath does not exist in the resources. Therefore, this patch can not succeed.")
                Files.copy(
                    fromPath,
                    toPath,
                    StandardCopyOption.REPLACE_EXISTING
                )
            }
        }

        return PatchResult.Success
    }
}
