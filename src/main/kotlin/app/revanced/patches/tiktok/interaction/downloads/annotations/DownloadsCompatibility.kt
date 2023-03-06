package app.revanced.patches.tiktok.interaction.downloads.annotations

import app.revanced.patcher.annotation.Compatibility
import app.revanced.patcher.annotation.Package

@Compatibility(
    [
        Package("com.ss.android.ugc.trill", arrayOf("27.8.3", "28.5.4")),
        Package("com.zhiliaoapp.musically", arrayOf("27.8.3", "28.5.4"))
    ]
)
@Target(AnnotationTarget.CLASS)
internal annotation class DownloadsCompatibility
