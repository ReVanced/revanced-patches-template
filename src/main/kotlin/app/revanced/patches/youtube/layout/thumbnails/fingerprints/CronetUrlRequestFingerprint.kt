package app.revanced.patches.youtube.layout.thumbnails.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.MethodFingerprint
import app.revanced.patches.youtube.layout.thumbnails.fingerprints.CronetUrlRequestFingerprint.IMPLEMENTATION_CLASS_NAME
import com.android.tools.smali.dexlib2.AccessFlags

internal object CronetUrlRequestFingerprint : MethodFingerprint(
    returnType = "V",
    accessFlags = AccessFlags.PUBLIC or AccessFlags.CONSTRUCTOR,
    customFingerprint = { _, classDef ->
        classDef.type == IMPLEMENTATION_CLASS_NAME
    }
) {
    const val IMPLEMENTATION_CLASS_NAME = "Lorg/chromium/net/impl/CronetUrlRequest;"
}