package app.revanced.patches.reddit.misc.uriparameters.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.Opcode

object ShareLinkFactoryFingerprint : MethodFingerprint(
    returnType = "L",
    access = AccessFlags.PUBLIC or AccessFlags.FINAL,
    opcodes = listOf(
        Opcode.CONST_STRING,
        Opcode.CONST_STRING,
        Opcode.INVOKE_DIRECT,
        Opcode.APUT_OBJECT,
        Opcode.INVOKE_STATIC,
        Opcode.MOVE_RESULT_OBJECT,
        Opcode.INVOKE_STATIC, // returnType: Ljava/lang/String;
        Opcode.MOVE_RESULT_OBJECT
    ),
    customFingerprint = { it.definingClass.endsWith("ShareLinkFactory;") }
)