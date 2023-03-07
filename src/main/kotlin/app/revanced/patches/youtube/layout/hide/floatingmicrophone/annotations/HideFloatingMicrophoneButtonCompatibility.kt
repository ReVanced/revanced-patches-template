package app.revanced.patches.youtube.layout.hide.floatingmicrophone.annotations

import app.revanced.patcher.annotation.Compatibility
import app.revanced.patcher.annotation.Package

@Compatibility(
    [Package(
        "com.google.android.youtube", arrayOf("17.49.37", "18.03.36")
    )]
)
@Target(AnnotationTarget.CLASS)
internal annotation class HideFloatingMicrophoneButtonCompatibility
