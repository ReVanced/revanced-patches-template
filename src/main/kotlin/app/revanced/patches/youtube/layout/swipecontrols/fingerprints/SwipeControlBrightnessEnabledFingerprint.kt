package app.revanced.patches.youtube.layout.swipecontrols.fingerprints

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.annotation.FuzzyPatternScanMethod
import app.revanced.patcher.fingerprint.method.annotation.MatchingMethod
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patches.youtube.layout.swipecontrols.annotations.SwipecontrolsCompatibility
import org.jf.dexlib2.AccessFlags

@Name("swipecontrolbrightnessenabled-signature")
@MatchingMethod(
    "Ljvy;", "<init>"
)
@FuzzyPatternScanMethod(3)
@SwipecontrolsCompatibility
@Version("0.0.1")
object SwipeControlBrightnessEnabledFingerprint : MethodFingerprint (
    "V", AccessFlags.PUBLIC or AccessFlags.FINAL, listOf("I", "I", "I", "I"),null, null
)