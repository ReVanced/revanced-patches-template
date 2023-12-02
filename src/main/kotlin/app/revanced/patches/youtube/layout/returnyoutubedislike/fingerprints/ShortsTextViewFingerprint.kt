package app.revanced.patches.youtube.layout.returnyoutubedislike.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.MethodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

internal object ShortsTextViewFingerprint : MethodFingerprint(
    accessFlags = AccessFlags.PUBLIC or AccessFlags.FINAL,
    returnType = "V",
    parameters = listOf("L", "L"),
    opcodes = listOf(
        Opcode.INVOKE_SUPER,    // first instruction of method
        Opcode.IF_NEZ,
        null,
        Opcode.INVOKE_VIRTUAL,
        Opcode.MOVE_RESULT_OBJECT,
        Opcode.CHECK_CAST,
        Opcode.SGET_OBJECT,     // insertion point, must be after constructor call to parent class
        Opcode.INVOKE_VIRTUAL,
        Opcode.MOVE_RESULT,
        Opcode.CONST_4,
        Opcode.IF_EQZ,
        Opcode.CONST_4,
        Opcode.IF_EQ,
        Opcode.CONST_4,
        Opcode.IF_EQ,
        Opcode.RETURN_VOID,
        Opcode.IGET_OBJECT,     // TextView field
        Opcode.IGET_BOOLEAN,    // boolean field
    )
)