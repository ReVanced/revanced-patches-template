package app.revanced.patches.youtube.layout.dearrow.bytecode.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.Opcode

object ImageUrlStringToURIFingerprint : MethodFingerprint(
    returnType = "Landroid/net/Uri;",
    accessFlags = AccessFlags.PUBLIC or AccessFlags.STATIC,
    parameters =  listOf("Ljava/lang/String;"),
    opcodes = listOf(
        Opcode.CONST_STRING,
        Opcode.INVOKE_VIRTUAL,
        Opcode.MOVE_RESULT_OBJECT,
        Opcode.INVOKE_VIRTUAL,
        Opcode.MOVE_RESULT_OBJECT,
        Opcode.RETURN_OBJECT,
    ),
    strings = listOf("https"),
)