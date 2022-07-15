package app.revanced.patches.youtube.layout.returnyoutubedislikes.fingerprints

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.fingerprint.method.annotation.DirectPatternScanMethod
import app.revanced.patcher.fingerprint.method.annotation.MatchingMethod
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patches.youtube.layout.returnyoutubedislikes.annotations.RYDCompatibility

@Name("text-component-spec-parent-fingerprint")
@MatchingMethod(
    "Lnvy;", "e"
)
@DirectPatternScanMethod
@RYDCompatibility
@Version("0.0.1")
object TextComponentSpecParentFingerprint : MethodFingerprint(
    null,
    null,
    null,
    null,
    listOf("TextComponentSpec: No converter for extension: ")
)