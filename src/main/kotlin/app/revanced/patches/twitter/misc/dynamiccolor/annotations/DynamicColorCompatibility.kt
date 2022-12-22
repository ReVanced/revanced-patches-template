package app.revanced.patches.twitter.misc.dynamiccolor.annotations

import app.revanced.patcher.annotation.Compatibility
import app.revanced.patcher.annotation.Package


@Compatibility(
    [Package(
        "com.twitter.android", arrayOf("1.11.111")
    )]
)
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
internal annotation class DynamicColorCompatibility