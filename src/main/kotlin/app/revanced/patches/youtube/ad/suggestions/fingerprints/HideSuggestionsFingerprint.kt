package app.revanced.patches.youtube.ad.suggestions.fingerprints

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.annotation.FuzzyPatternScanMethod
import app.revanced.patcher.fingerprint.method.annotation.MatchingMethod
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patches.youtube.ad.suggestions.annotations.HideSuggestionsCompatibility
import app.revanced.patches.youtube.layout.oldqualitylayout.annotations.OldQualityLayoutCompatibility
import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.Opcode

@Name("hide-infocard-fingerprint")
@MatchingMethod("Liff;", "i")
@FuzzyPatternScanMethod(2)
@HideSuggestionsCompatibility
@Version("0.0.1")
object HideSuggestionsFingerprint : MethodFingerprint(
    "Ljava/lang/Boolean;",
    AccessFlags.PUBLIC or AccessFlags.FINAL,
    null,
    null,
    listOf("vibrator"),
    null
)