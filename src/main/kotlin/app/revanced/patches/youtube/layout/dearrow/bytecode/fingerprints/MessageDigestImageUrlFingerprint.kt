package app.revanced.patches.youtube.layout.dearrow.bytecode.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import org.jf.dexlib2.AccessFlags

object MessageDigestImageUrlFingerprint : MethodFingerprint(
    accessFlags = AccessFlags.PUBLIC or AccessFlags.CONSTRUCTOR,
    parameters =  listOf("Ljava/lang/String;", "L")
)