package app.revanced.patches.youtube.layout.sponsorblock.annotations

import app.revanced.patcher.annotation.Compatibility
import app.revanced.patcher.annotation.Package

@Compatibility(
    [Package("com.google.android.youtube", arrayOf("18.08.37", "18.15.40"))]
)
@Target(AnnotationTarget.CLASS)
internal annotation class SponsorBlockCompatibility