package app.revanced.patches.youtube.layout.returnyoutubedislike.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import org.jf.dexlib2.Opcode

object TextComponentSpecExtensionFingerprint : MethodFingerprint(
    returnType = "L",
    parameters = listOf("L", "L", "L", "L", "L", "L", "L", "L", "Z", "Z", "Z"),
    opcodes = listOf(
        Opcode.INVOKE_STATIC,
        Opcode.MOVE_RESULT_OBJECT,
        Opcode.CONST_4
    ),
    strings = listOf("TextComponentSpec: No converter for extension: %s")
)