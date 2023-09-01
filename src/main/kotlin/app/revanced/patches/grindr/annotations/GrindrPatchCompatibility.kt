package app.revanced.patches.grindr.annotations

import app.revanced.patcher.annotation.Compatibility
import app.revanced.patcher.annotation.Package

@Compatibility([Package("com.grindrapp.android")])
@Target(AnnotationTarget.CLASS)
internal annotation class GrindrPatchCompatibility