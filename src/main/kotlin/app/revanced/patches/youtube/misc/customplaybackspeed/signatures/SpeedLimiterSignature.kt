package app.revanced.patches.youtube.misc.customplaybackspeed.signatures

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.extensions.or
import app.revanced.patcher.signature.implementation.method.MethodSignature
import app.revanced.patcher.signature.implementation.method.annotation.FuzzyPatternScanMethod
import app.revanced.patcher.signature.implementation.method.annotation.MatchingMethod
import app.revanced.patches.youtube.misc.customplaybackspeed.annotations.CustomPlaybackSpeedCompatibility
import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.Opcode

@Name("speed-limiter-signature")
@MatchingMethod(
    "Lxgy", "y"
)
@FuzzyPatternScanMethod(2) 
@CustomPlaybackSpeedCompatibility
@Version("0.0.1")
object SpeedLimiterSignature : MethodSignature(
    "V",
    AccessFlags.PUBLIC or AccessFlags.FINAL,
    listOf("F"),
    listOf(
        Opcode.INVOKE_STATIC,
        Opcode.MOVE_RESULT,
        Opcode.IF_EQZ,
        Opcode.CONST_HIGH16,
        Opcode.GOTO,
        Opcode.CONST_HIGH16,
        Opcode.CONST_HIGH16,
        Opcode.INVOKE_STATIC,
    ),
    null
)
