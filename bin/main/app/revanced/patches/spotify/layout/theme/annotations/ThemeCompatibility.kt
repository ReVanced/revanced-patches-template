package app.revanced.patches.spotify.layout.theme.annotations

import app.revanced.patcher.annotation.Compatibility
import app.revanced.patcher.annotation.Package

@Compatibility([Package("com.spotify.music")])
@Target(AnnotationTarget.CLASS)
internal annotation class ThemeCompatibility
