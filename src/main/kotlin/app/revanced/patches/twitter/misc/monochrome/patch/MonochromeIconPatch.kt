package app.revanced.patches.twitter.misc.monochrome.patch

import app.revanced.patcher.ResourceContext
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.twitter.misc.monochrome.annotations.MonochromeIconCompatibility
import java.lang.StringBuilder
import java.nio.file.Files

@Patch
@Name("monochrome-icon")
@Description("Adds a monochrome icon.")
@MonochromeIconCompatibility
@Version("0.0.1")
class MonochromeIconPatch : ResourcePatch {
    override fun execute(context: ResourceContext): PatchResult {
        val resDirectory = context.getPath("res", context.apkBundle.base)!!
        // if (!resDirectory.isDirectory) return PatchResult.Error("The res folder can not be found.")

        val mipmapV33Directory = resDirectory.resolve("mipmap-anydpi-v33")
        // if (!mipmapV33Directory.isDirectory) Files.createDirectories(mipmapV33Directory)
        Files.createDirectories(mipmapV33Directory)
        Files.write(
            mipmapV33Directory.resolve("ic_launcher_twitter.xml"),
            listOf(StringBuffer().apply {
                append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n")
                append(        "<adaptive-icon\n")
                append(        "  xmlns:android=\"http://schemas.android.com/apk/res/android\">\n")
                append(        "    <background android:drawable=\"@color/ic_launcher_background\" />\n")
                append(        "    <foreground android:drawable=\"@mipmap/ic_launcher_twitter_foreground\" />\n")
                append(        "    <monochrome android:drawable=\"@mipmap/ic_launcher_twitter_foreground\" />\n")
                append(        "</adaptive-icon>")
            })
        )

        Files.write(
            mipmapV33Directory.resolve("ic_launcher_twitter_round.xml"),
            listOf(StringBuilder().apply {
                append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n")
                append(        "<adaptive-icon\n")
                append(        "  xmlns:android=\"http://schemas.android.com/apk/res/android\">\n")
                append(        "    <background android:drawable=\"@color/ic_launcher_background\" />\n")
                append(        "    <foreground android:drawable=\"@mipmap/ic_launcher_twitter_foreground\" />\n")
                append(        "    <monochrome android:drawable=\"@mipmap/ic_launcher_twitter_foreground\" />\n")
                append(        "</adaptive-icon>")
            })
        )

        return PatchResult.Success
    }
}