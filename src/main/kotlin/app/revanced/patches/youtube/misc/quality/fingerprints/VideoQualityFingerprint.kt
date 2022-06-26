package app.revanced.patches.youtube.misc.quality.fingerprints

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patcher.fingerprint.method.annotation.DirectPatternScanMethod
import app.revanced.patcher.fingerprint.method.annotation.MatchingMethod
import app.revanced.patches.youtube.misc.quality.annotations.DefaultVideoQualityCompatibility
import org.jf.dexlib2.Opcode

@Name("video-quality-fingerprint")
@MatchingMethod("Lkdy;", "b")
@DirectPatternScanMethod
@DefaultVideoQualityCompatibility
@Version("0.0.1")
object VideoQualityFingerprint : MethodFingerprint(
    null, null, null, listOf(
        Opcode.IPUT_OBJECT, Opcode.RETURN
    )
)