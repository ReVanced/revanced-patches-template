package app.revanced.patches.youtube.layout.returnyoutubedislike.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.Opcode

object TextComponentContextFingerprint : MethodFingerprint(
    returnType = "L",
    access = AccessFlags.PROTECTED or AccessFlags.FINAL,
    parameters = listOf("L"),
    opcodes = listOf(
        Opcode.IGET_OBJECT, // conversion context
        Opcode.IGET_OBJECT,
        Opcode.IGET_OBJECT,
        Opcode.IGET_BOOLEAN,
        Opcode.IGET,
        Opcode.IGET,
        Opcode.IGET,

        // here lies a bunch of opcodes of no interest

//        Opcode.IGET_OBJECT,
//        Opcode.MOVE_FROM16,
//        Opcode.INVOKE_DIRECT_RANGE,
//        Opcode.MOVE_RESULT_OBJECT,
//        Opcode.IGET_OBJECT,
//        Opcode.MOVE_OBJECT_FROM16, // CharSequence atomic reference
    )
)