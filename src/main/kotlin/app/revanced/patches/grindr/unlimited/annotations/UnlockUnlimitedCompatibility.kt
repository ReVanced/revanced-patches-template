package app.revanced.patches.grindr.unlimited.annotations

import app.revanced.patcher.annotation.Compatibility
import app.revanced.patcher.annotation.Package

@Compatibility([Package("com.grindrapp.android", arrayOf("9.14.0", "9.15.0", "9.16.0"))])
@Target(AnnotationTarget.CLASS)
internal annotation class UnlockUnlimitedCompatibility

