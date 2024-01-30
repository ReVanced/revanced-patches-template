package app.revanced.patches.grindr.unlimited.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.annotation.FuzzyPatternScanMethod
import app.revanced.patcher.fingerprint.MethodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

object IsNoPlusUpsellFingerprint : MethodFingerprint(
    "Z",
    accessFlags = AccessFlags.PUBLIC.value,
    opcodes = listOf(
        Opcode.INVOKE_VIRTUAL,
        Opcode.MOVE_RESULT,
        Opcode.IF_NEZ,
        Opcode.INVOKE_VIRTUAL,
        Opcode.MOVE_RESULT,
        Opcode.IF_NEZ,
        Opcode.INVOKE_VIRTUAL,
        Opcode.MOVE_RESULT,
        Opcode.IF_EQZ,
        Opcode.GOTO,
        Opcode.CONST_4,
        Opcode.GOTO,
        Opcode.CONST_4,
        Opcode.RETURN
    ),
)