package app.revanced.patches.youtube.layout.swipecontrols.fingerprints

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.annotation.FuzzyPatternScanMethod
import app.revanced.patcher.fingerprint.method.annotation.MatchingMethod
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patches.youtube.layout.swipecontrols.annotations.SwipecontrolsCompatibility
import org.jf.dexlib2.AccessFlags

@Name("YouTubePlayerOverlaysLayout-parent-fingerprint")
@MatchingMethod(
    "Ljvy;", "<init>"
)
@FuzzyPatternScanMethod(3)
@SwipecontrolsCompatibility
@Version("0.0.1")
object YouTubePlayerOverlaysLayoutConstructorFingerprint : MethodFingerprint (
    null, AccessFlags.PUBLIC or AccessFlags.CONSTRUCTOR, listOf("Landroid/content/Context;"),null, null, { methodDef -> methodDef.definingClass.contains("YouTubePlayerOverlaysLayout") }
)