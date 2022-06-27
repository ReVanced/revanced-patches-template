package app.revanced.patches.youtube.layout.swipecontrols.fingerprints

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.fingerprint.method.annotation.FuzzyPatternScanMethod
import app.revanced.patcher.fingerprint.method.annotation.MatchingMethod
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patches.youtube.layout.swipecontrols.annotations.SwipecontrolsCompatibility

@Name("swipecontrolbrightnessenabled-parent-fingerprint")
@MatchingMethod(
    "Ljvy;", "<init>"
)
@FuzzyPatternScanMethod(3)
@SwipecontrolsCompatibility
@Version("0.0.1")
object SwipeControlBrightnessEnabledParentFingerprint : MethodFingerprint (
    "V", null, null,null, listOf("mediaViewambientBrightnessSensor")
)