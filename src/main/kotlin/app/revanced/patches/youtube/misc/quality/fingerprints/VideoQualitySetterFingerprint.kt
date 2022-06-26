
package app.revanced.patches.youtube.misc.quality.fingerprints

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patcher.fingerprint.method.annotation.DirectPatternScanMethod
import app.revanced.patcher.fingerprint.method.annotation.MatchingMethod
import app.revanced.patches.youtube.misc.quality.annotations.DefaultVideoQualityCompatibility
import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.Opcode

@Name("video-quality-setter-fingerprint")
@MatchingMethod("Lkdy;", "a")
@DirectPatternScanMethod
@DefaultVideoQualityCompatibility
@Version("0.0.1")
object VideoQualitySetterFingerprint : MethodFingerprint(
    "V",
    AccessFlags.PUBLIC or AccessFlags.FINAL,
    listOf("L","I","I","Z","I"),
    listOf(
        Opcode.IGET_OBJECT,
        Opcode.IF_EQ,
        Opcode.IGET,
        Opcode.IF_NE,
        Opcode.GOTO,
    )
)