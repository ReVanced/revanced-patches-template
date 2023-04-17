package app.revanced.patches.youtube.layout.returnyoutubedislike.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import org.jf.dexlib2.Opcode

object TextComponentSpecFingerprint : MethodFingerprint(
    returnType = "Ljava/lang/CharSequence;",
    parameters = listOf("L", "L", "L", "L", "L", "L", "L", "L", "Z", "Z", "Z"),
    opcodes = listOf(
        Opcode.NEW_INSTANCE,
        Opcode.INVOKE_DIRECT,
        Opcode.INVOKE_VIRTUAL_RANGE,
        Opcode.MOVE_RESULT,
        Opcode.INVOKE_STATIC,
        Opcode.RETURN_OBJECT, // last instruction of the method
    ),
    strings = listOf("TextComponentSpec: No converter for extension: %s")
)