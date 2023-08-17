package app.revanced.patches.youtube.layout.alternativethumbnails.bytecode.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import org.jf.dexlib2.AccessFlags

object CronetURLRequestCallbackOnFailureFingerprint : MethodFingerprint(
    returnType = "V",
    accessFlags = AccessFlags.PUBLIC or AccessFlags.FINAL,
    parameters =  listOf("Lorg/chromium/net/UrlRequest;", "Lorg/chromium/net/UrlResponseInfo;", "Lorg/chromium/net/CronetException;"),
    customFingerprint = { methodDef, _ ->
        methodDef.name == "onFailed"
    }
)