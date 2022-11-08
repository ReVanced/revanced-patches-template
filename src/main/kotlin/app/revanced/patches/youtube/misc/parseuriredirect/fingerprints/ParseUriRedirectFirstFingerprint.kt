package app.revanced.patches.youtube.misc.parseuriredirect.fingerprints

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patches.youtube.misc.parseuriredirect.annotations.ParseUriRedirectCompatibility
import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.Opcode

@Name("parse-uri-redirect-first-fingerprint")
@ParseUriRedirectCompatibility
@Version("0.0.1")
object ParseUriRedirectFirstFingerprint : MethodFingerprint(
    "Ljava/lang/Object", AccessFlags.PUBLIC or AccessFlags.FINAL, listOf("L"), listOf(
        Opcode.CHECK_CAST,
        Opcode.INVOKE_STATIC,
        Opcode.MOVE_RESULT_OBJECT,
        Opcode.RETURN_OBJECT,
        Opcode.CHECK_CAST,
        Opcode.SGET
    )
)