package app.revanced.patches.youtube.layout.branding.header

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.PatchException
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patcher.patch.options.types.BooleanPatchOption.Companion.booleanPatchOption
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import kotlin.io.path.exists

@Patch(
    name = "Premium heading",
    description = "Controls whether the premium heading should be shown.",
    compatiblePackages = [
        CompatiblePackage("com.google.android.youtube")
    ]
)
@Suppress("unused")
object PremiumHeadingPatch : ResourcePatch() {
    private const val DEFAULT_HEADING_RES = "yt_wordmark_header"
    private const val PREMIUM_HEADING_RES = "yt_premium_wordmark_header"

    private val useDefaultHeading by booleanPatchOption(
        key = "useDefaultHeading",
        default = false,
        title = "Use default heading",
        description = "Whether to use the default heading instead of the premium one."
    )

    override fun execute(context: ResourceContext) {
        val resDirectory = context["res"]
        if (!resDirectory.isDirectory) throw PatchException("The res folder can not be found.")

        val (original, replacement) = if (useDefaultHeading != true) PREMIUM_HEADING_RES to DEFAULT_HEADING_RES
        else DEFAULT_HEADING_RES to PREMIUM_HEADING_RES
        val modes = arrayOf("light", "dark")

        arrayOf("xxxhdpi", "xxhdpi", "xhdpi", "hdpi", "mdpi").forEach { size ->
            val headingDirectory = resDirectory.resolve("drawable-$size")
            modes.forEach { mode ->
                val fromPath = headingDirectory.resolve("${original}_$mode.png").toPath()
                val toPath = headingDirectory.resolve("${replacement}_$mode.png").toPath()

                if (!fromPath.exists())
                    throw PatchException("The file $fromPath does not exist in the resources. Therefore, this patch can not succeed.")
                Files.copy(
                    fromPath,
                    toPath,
                    StandardCopyOption.REPLACE_EXISTING
                )
            }
        }
    }
}
