package app.revanced.patches.youtube.layout.branding.header.patch

import app.revanced.patcher.ResourceContext
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.youtube.layout.branding.header.annotations.PremiumHeadingCompatibility

@Patch
@Name("premium-heading")
@Description("Shows premium branding on the home screen.")
@PremiumHeadingCompatibility
@Version("0.0.1")
class PremiumHeadingPatch : ResourcePatch {
    override fun execute(context: ResourceContext): PatchResult {
        val (original, replacement) = "yt_premium_wordmark_header" to "yt_wordmark_header"
        val modes = arrayOf("light", "dark")

        arrayOf("xxxhdpi", "xxhdpi", "xhdpi", "hdpi", "mdpi").forEach dpi@{ size ->
            val headingDirectory = "res/drawable-$size"
            val target = context.apkBundle.resources.query(size)
            modes.forEach { mode ->
                TODO("take if exists")
                (target.openFile("$headingDirectory/${original}_$mode.png") ?: return@dpi).use { from ->
                    target.openFile("$headingDirectory/${replacement}_$mode.png").use { to ->
                        to.contents = from.contents
                    }
                }
            }
        }

        return PatchResult.Success
    }
}
