package app.revanced.patches.tiktok.interaction.speed.annotations

import app.revanced.patcher.annotation.Compatibility
import app.revanced.patcher.annotation.Package

@Compatibility(
    [
        Package("com.ss.android.ugc.trill"),
        Package("com.zhiliaoapp.musically")
    ]
)
@Target(AnnotationTarget.CLASS)
internal annotation class PlaybackSpeedCompatibility
