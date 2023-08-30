package app.revanced.patches.youtube.layout.branding.header.annotations

import app.revanced.patcher.annotation.Compatibility
import app.revanced.patcher.annotation.Package

@Compatibility([Package("com.google.android.youtube")])
@Target(AnnotationTarget.CLASS)
internal annotation class PremiumHeadingCompatibility
