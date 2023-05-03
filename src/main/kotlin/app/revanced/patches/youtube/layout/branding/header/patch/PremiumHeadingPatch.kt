package app.revanced.patches.youtube.layout.branding.header.patch

import app.revanced.patcher.ResourceContext
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.arsc.reference
import app.revanced.patcher.patch.PatchException
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

        modes.forEach { mode ->
            val resource = reference(context.apkBundle.resources.resolve("drawable", "${original}_$mode"))
            arrayOf("xxxhdpi", "xxhdpi", "xhdpi", "hdpi", "mdpi").forEach dpi@{ size ->
                val target = context.apkBundle.resources.query(size)
                target.set("drawable", "${replacement}_$mode", resource, size)
            }
        }

        return PatchResult.Success
    }
}
