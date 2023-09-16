package app.revanced.patches.strava.annotations

import app.revanced.patcher.annotation.Compatibility
import app.revanced.patcher.annotation.Package

@Compatibility([Package("com.strava", ["324.10"])])
@Target(AnnotationTarget.CLASS)
internal annotation class StravaCompatibility
