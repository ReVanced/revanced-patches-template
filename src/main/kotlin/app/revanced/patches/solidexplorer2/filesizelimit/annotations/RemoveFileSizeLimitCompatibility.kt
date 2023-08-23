package app.revanced.patches.solidexplorer2.filesizelimit.annotations

import app.revanced.patcher.annotation.Compatibility
import app.revanced.patcher.annotation.Package

@Compatibility([Package("pl.solidexplorer2")])
@Target(AnnotationTarget.CLASS)
internal annotation class RemoveFileSizeLimitCompatibility
