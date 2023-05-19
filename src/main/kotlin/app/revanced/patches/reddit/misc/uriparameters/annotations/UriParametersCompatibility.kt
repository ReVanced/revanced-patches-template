package app.revanced.patches.reddit.misc.uriparameters.annotations

import app.revanced.patcher.annotation.Compatibility
import app.revanced.patcher.annotation.Package

@Compatibility(
    [Package(
        "com.reddit.frontpage", arrayOf(
            "2023.12.0",
            "2023.17.1"
        )
    )]
)
@Target(AnnotationTarget.CLASS)
internal annotation class UriParametersCompatibility
