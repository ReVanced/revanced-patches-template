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

@Name("speed-array-generator-signature")
@MatchingMethod(
    "Lzdj", "d"
)
@FuzzyPatternScanMethod(2) 
@CustomPlaybackSpeedCompatibility
@Version("0.0.1")
object ArrayGeneratorSignature : MethodSignature(
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
