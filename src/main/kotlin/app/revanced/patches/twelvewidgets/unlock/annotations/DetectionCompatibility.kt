package app.revanced.patches.twelvewidgets.unlock.annotations

import app.revanced.patcher.annotation.Compatibility
import app.revanced.patcher.annotation.Package

@Compatibility([Package("com.dci.dev.androidtwelvewidgets", arrayOf("1.4.1"))])
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
internal annotation class DetectionCompatibility
