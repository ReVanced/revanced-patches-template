package app.revanced.patches.youtube.layout.dearrow.bytecode.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.Opcode

object ImageUrlStringToURIFingerprint : MethodFingerprint(
//    returnType = "Landroid/net/Uri;",
    accessFlags = AccessFlags.PUBLIC or AccessFlags.CONSTRUCTOR,
    parameters =  listOf("Lorg/chromium/net/impl/CronetUrlRequestContext;", "Ljava/lang/String;", "I",
        "Lorg/chromium/net/UrlRequest\$Callback;", "Ljava/util/concurrent/Executor;", "Ljava/util/Collection;",
        "Z", "Z", "Z", "Z", "I", "Z", "I", "Lorg/chromium/net/RequestFinishedInfo\$Listener;", "I", "J"),
//    opcodes = listOf(
//        Opcode.CONST_STRING,
//        Opcode.INVOKE_VIRTUAL,
//        Opcode.MOVE_RESULT_OBJECT,
//        Opcode.INVOKE_VIRTUAL,
//        Opcode.MOVE_RESULT_OBJECT,
//        Opcode.RETURN_OBJECT,
//    ),
//    strings = listOf("https"),
    customFingerprint = { methodDef, _ ->
        methodDef.definingClass == "Lorg/chromium/net/impl/CronetUrlRequest;"
    }
)