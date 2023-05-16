package app.revanced.patches.vsco.misc.pro.annotations

import app.revanced.patcher.annotation.Compatibility
import app.revanced.patcher.annotation.Package

@Compatibility(
    [Package(
        "com.vsco.cam", arrayOf(
            "319",
        )
    )]
)
internal annotation class ProUnlockCompatibility
