package app.revanced.patches.youtube.layout.branding.icon.annotations

import app.revanced.patcher.annotation.Compatibility
import app.revanced.patcher.annotation.Package

@Compatibility([Package("com.google.android.youtube"), Package("com.google.android.apps.youtube.music")])
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
internal annotation class CustomBrandingCompatibility
