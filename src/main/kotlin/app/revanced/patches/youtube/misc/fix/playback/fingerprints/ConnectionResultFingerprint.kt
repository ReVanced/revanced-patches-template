package app.revanced.patches.youtube.misc.fix.playback.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.Opcode

object ConnectionResultFingerprint : MethodFingerprint(
    returnType = "J",
    parameters = listOf("L"),
    access = AccessFlags.PUBLIC or AccessFlags.FINAL,
    strings = listOf(
        "err_cleartext_not_permitted",
        "Content-Range",
        "Content-Type",
        "Content-Encoding",
        "Content-Length"
    ),
    opcodes = listOf(
        Opcode.IF_EQZ,
        Opcode.IGET_OBJECT,
        Opcode.INVOKE_STATIC,
        Opcode.INVOKE_VIRTUAL,
        Opcode.MOVE_RESULT,         // http status code
        Opcode.INVOKE_VIRTUAL,
        Opcode.MOVE_RESULT_OBJECT,  // url headers
    )
)