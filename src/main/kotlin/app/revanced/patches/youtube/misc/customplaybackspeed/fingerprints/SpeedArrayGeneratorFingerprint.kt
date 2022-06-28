package app.revanced.patches.youtube.misc.customplaybackspeed.fingerprints

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patcher.fingerprint.method.annotation.FuzzyPatternScanMethod
import app.revanced.patcher.fingerprint.method.annotation.MatchingMethod
import app.revanced.patches.youtube.misc.customplaybackspeed.annotations.CustomPlaybackSpeedCompatibility
import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.Opcode

@Name("speed-array-generator-fingerprint")
@MatchingMethod(
    "Lzdj;", "d"
)
@FuzzyPatternScanMethod(2)
@CustomPlaybackSpeedCompatibility
@Version("0.0.1")
object SpeedArrayGeneratorFingerprint : MethodFingerprint(
    "[L",
    AccessFlags.PUBLIC or AccessFlags.STATIC,
    null,
    listOf(
        Opcode.IF_NEZ,
        Opcode.SGET_OBJECT,
        Opcode.GOTO,
        Opcode.INVOKE_INTERFACE,
        Opcode.MOVE_RESULT_OBJECT,
        Opcode.IGET_OBJECT,
    ),
    listOf("0.0#")
)
