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

@Name("hide-suggestions-fingerprint")
@MatchingMethod(definingClass = "Liff;", name = "e")
@FuzzyPatternScanMethod(2)
@HideSuggestionsCompatibility
@Version("0.0.1")
object HideSuggestionsFingerprint : MethodFingerprint(
    "V", null, null, null,null
)