package app.revanced.patches.music.layout.branding.icon.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.impl.ResourceData
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultError
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.Dependencies
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.patch.impl.ResourcePatch
import app.revanced.patches.music.layout.branding.icon.annotations.CustomBrandingMusicCompatibility
import java.nio.file.Files

@Patch
@Name("custom-branding-music")
@Description("Changes the YouTube Music launcher icon to be ReVanced's.")
@CustomBrandingMusicCompatibility
@Version("0.0.1")
class CustomMusicBrandingPatch : ResourcePatch() {
    override fun execute(data: ResourceData): PatchResult {
        val resDirectory = data["res"]
        if (!resDirectory.isDirectory) return PatchResultError("The res folder can not be found.")

        val iconNames = arrayOf(
            "adaptiveproduct_youtube_music_background_color_108",
            "adaptiveproduct_youtube_music_foreground_color_108",
            "ic_launcher",
            "ic_launcher_round"
        )

        mapOf(
            "xxxhdpi" to 192,
            "xxhdpi" to 144,
            "xhdpi" to 96,
            "hdpi" to 72,
            "mdpi" to 48
        ).forEach { (iconDirectory, size) ->
            iconNames.forEach iconLoop@{ iconName ->
                val iconFile = this.javaClass.classLoader.getResourceAsStream("branding/music/$size/$iconName.png")
                    ?: return PatchResultError("The icon $iconName can not be found.")

                Files.write(
                    resDirectory.resolve("mipmap-$iconDirectory").resolve("$iconName.png").toPath(), iconFile.readAllBytes()
                )
            }
        }

        return PatchResultSuccess()
    }
}