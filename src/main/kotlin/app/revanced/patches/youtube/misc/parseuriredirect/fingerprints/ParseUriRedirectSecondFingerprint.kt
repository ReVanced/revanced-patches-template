package app.revanced.patches.youtube.misc.parseuriredirect.fingerprints

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patches.youtube.misc.parseuriredirect.annotations.ParseUriRedirectCompatibility
import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.Opcode

@Name("parse-uri-redirect-second-fingerprint")
@ParseUriRedirectCompatibility
@Version("0.0.1")
object ParseUriRedirectSecondFingerprint : MethodFingerprint(
    "L", AccessFlags.PUBLIC or AccessFlags.STATIC, listOf("L"), listOf(
        Opcode.INVOKE_STATIC,
        Opcode.MOVE_RESULT_OBJECT,
        Opcode.INVOKE_STATIC,
        Opcode.MOVE_RESULT_OBJECT,
        Opcode.INVOKE_VIRTUAL,
        Opcode.MOVE_RESULT,
        Opcode.IF_EQZ,
        Opcode.RETURN_OBJECT,
        Opcode.NEW_INSTANCE,
        Opcode.CONST_STRING
    ),
    strings = listOf("Uri must have an absolute scheme")
)