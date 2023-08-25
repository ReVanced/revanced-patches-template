package app.revanced.patches.twitter.misc.dynamiccolor.annotations

import app.revanced.patcher.annotation.Compatibility
import app.revanced.patcher.annotation.Package

@Compatibility([Package("com.twitter.android")])
@Target(AnnotationTarget.CLASS)
internal annotation class DynamicColorCompatibility