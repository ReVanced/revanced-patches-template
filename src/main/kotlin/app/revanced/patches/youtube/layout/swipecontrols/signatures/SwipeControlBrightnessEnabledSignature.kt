package app.revanced.patches.youtube.layout.swipecontrols.signatures

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.extensions.or
import app.revanced.patcher.signature.implementation.method.MethodSignature
import app.revanced.patcher.signature.implementation.method.annotation.FuzzyPatternScanMethod
import app.revanced.patcher.signature.implementation.method.annotation.MatchingMethod
import app.revanced.patches.youtube.layout.swipecontrols.annotations.SwipecontrolsCompatibility
import org.jf.dexlib2.AccessFlags

@Name("swipecontrolbrightnessenabled-signature")
@MatchingMethod(
    "Ljvy;", "<init>"
)
@FuzzyPatternScanMethod(3)
@SwipecontrolsCompatibility
@Version("0.0.1")
object SwipeControlBrightnessEnabledSignature : MethodSignature(
    null, AccessFlags.PUBLIC or AccessFlags.FINAL, listOf("I", "I", "I", "I"),null, null
)