package app.revanced.patches.truecaller.misc.subscription.annotations

import app.revanced.patcher.annotation.Compatibility
import app.revanced.patcher.annotation.Package

@Compatibility(
    [Package(
        "com.truecaller", arrayOf("12.51.7")
    )]
)
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
internal annotation class SubscriptionUnlockCompatibility
