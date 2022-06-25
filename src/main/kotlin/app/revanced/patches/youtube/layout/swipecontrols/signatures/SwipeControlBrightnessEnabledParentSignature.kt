package app.revanced.patches.youtube.layout.swipecontrols.signatures

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.signature.implementation.method.MethodSignature
import app.revanced.patcher.signature.implementation.method.annotation.FuzzyPatternScanMethod
import app.revanced.patcher.signature.implementation.method.annotation.MatchingMethod
import app.revanced.patches.youtube.layout.swipecontrols.annotations.SwipecontrolsCompatibility

@Name("swipecontrolbrightnessenabled-parent-signature")
@MatchingMethod(
    "Ljvy;", "<init>"
)
@FuzzyPatternScanMethod(3)
@SwipecontrolsCompatibility
@Version("0.0.1")
object SwipeControlBrightnessEnabledParentSignature : MethodSignature(
    "L", null, null,null, listOf("mediaViewambientBrightnessSensor")
)