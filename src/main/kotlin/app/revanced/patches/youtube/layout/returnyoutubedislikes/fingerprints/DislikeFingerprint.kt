package app.revanced.patches.youtube.layout.returnyoutubedislikes.fingerprints

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.annotation.FuzzyPatternScanMethod
import app.revanced.patcher.fingerprint.method.annotation.MatchingMethod
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patches.youtube.layout.returnyoutubedislikes.annotations.RYDCompatibility
import org.jf.dexlib2.AccessFlags

@Name("dislike-fingerprint")
@MatchingMethod(
    "Ltwt;", "<init>"
)
@FuzzyPatternScanMethod(2)
@RYDCompatibility
@Version("0.0.1")
object DislikeFingerprint : MethodFingerprint(
    "V",
    AccessFlags.PROTECTED or AccessFlags.CONSTRUCTOR,
    listOf("L", "L", "[B", "[B", "[B", "[B"),
    null,
    listOf("like/dislike")
)