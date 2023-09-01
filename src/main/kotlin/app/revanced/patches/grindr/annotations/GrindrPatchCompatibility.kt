package app.revanced.patches.grindr.annotations

import app.revanced.patcher.annotation.Compatibility
import app.revanced.patcher.annotation.Package

@Compatibility([Package("com.grindrapp.android", arrayOf("9.15.0"))])
@Target(AnnotationTarget.CLASS)
internal annotation class GrindrPatchCompatibility