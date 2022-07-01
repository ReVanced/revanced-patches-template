package app.revanced.patches.youtube.ad.suggestions.fingerprints

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patcher.fingerprint.method.annotation.FuzzyPatternScanMethod
import app.revanced.patcher.fingerprint.method.annotation.MatchingMethod
import app.revanced.patches.youtube.ad.suggestions.annotations.HideSuggestionsCompatibility
import app.revanced.patches.youtube.layout.oldqualitylayout.annotations.OldQualityLayoutCompatibility
import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.Opcode

@Name("hide-suggestions-parent-fingerprint")
@MatchingMethod(
    "Liff;", "lE"
)
@FuzzyPatternScanMethod(2)
@HideSuggestionsCompatibility
@Version("0.0.1")
object HideSuggestionsParentFingerprint : MethodFingerprint(
    "Ljava/lang/String",
    AccessFlags.PUBLIC or AccessFlags.FINAL,
    null,
    null,
    listOf("player_overlay_info_card_teaser"),
    null
)