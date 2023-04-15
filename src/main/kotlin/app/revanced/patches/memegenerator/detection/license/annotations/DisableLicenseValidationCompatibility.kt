package app.revanced.patches.memegenerator.detection.license.annotations

import app.revanced.patcher.annotation.Compatibility
import app.revanced.patcher.annotation.Package

@Compatibility([Package("com.zombodroid.MemeGenerator", arrayOf("4.6364"))])
@Target(AnnotationTarget.CLASS)
internal annotation class DisableLicenseValidationCompatibility
