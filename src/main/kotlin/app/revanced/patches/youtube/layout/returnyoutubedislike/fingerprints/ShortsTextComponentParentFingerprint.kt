package app.revanced.patches.youtube.layout.returnyoutubedislike.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.Opcode

object ShortsTextComponentParentFingerprint : MethodFingerprint(
    returnType = "V",
    access = AccessFlags.PROTECTED or AccessFlags.FINAL,
    parameters = listOf("L", "L"),
    opcodes = listOf(
        // instructions not interested in.  Only here to better ensure a correct match
        Opcode.IF_EQZ,
        Opcode.CONST_4,
        Opcode.IF_EQ,
        Opcode.CONST_4,
        Opcode.IF_EQ,
        Opcode.RETURN_VOID,

        // actual instructions interested in
        Opcode.IGET_OBJECT,
        Opcode.CHECK_CAST,
        Opcode.IGET_BOOLEAN,
        Opcode.IF_EQZ,
        Opcode.INVOKE_STATIC
    )

    // class also contains unique string "Failed to set parent screen", but string is inside a different method
)