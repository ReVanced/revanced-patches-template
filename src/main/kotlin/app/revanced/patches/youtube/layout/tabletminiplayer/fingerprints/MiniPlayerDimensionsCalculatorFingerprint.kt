package app.revanced.patches.youtube.layout.tabletminiplayer.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.Opcode

object MiniPlayerDimensionsCalculatorFingerprint : MethodFingerprint(
    accessFlags = AccessFlags.PUBLIC or AccessFlags.FINAL,
    returnType = "V",
    parameters = listOf("L"),
    opcodes = listOf(
        Opcode.INVOKE_DIRECT,
        Opcode.MOVE_RESULT,
        Opcode.CONST_4,
        Opcode.IF_NEZ,
        Opcode.FLOAT_TO_DOUBLE,
        Opcode.CONST_WIDE_HIGH16,
        Opcode.CMPL_DOUBLE,
    )
)