package app.revanced.patches.grindr.unlimited.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.annotation.FuzzyPatternScanMethod
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

object IsXtraFingerprint : MethodFingerprint(
    "Z",
    accessFlags = AccessFlags.PUBLIC.value,
    opcodes = listOf(
        Opcode.IGET_OBJECT,
        Opcode.CHECK_CAST,
        Opcode.CONST_4,
        Opcode.NEW_ARRAY,
        Opcode.CONST_4,
        Opcode.SGET_OBJECT,
        Opcode.APUT_OBJECT,
        Opcode.CONST_4,
        Opcode.SGET_OBJECT,
        Opcode.APUT_OBJECT,
        Opcode.INVOKE_STATIC,
        Opcode.MOVE_RESULT,
        Opcode.RETURN
    ),
    customFingerprint = { methodDef, _ ->
        methodDef.name.contains("p")
    }
)