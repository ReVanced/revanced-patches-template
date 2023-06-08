package app.revanced.patches.youtube.misc.links.open.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.Opcode

object OpenLinksDirectlyFingerprintPrimary : MethodFingerprint(
    returnType = "Ljava/lang/Object",
    access = AccessFlags.PUBLIC or AccessFlags.FINAL,
    parameters = listOf("Ljava/lang/Object"),
    opcodes = listOf(
        Opcode.INVOKE_STATIC,
        Opcode.MOVE_RESULT_OBJECT,
        Opcode.RETURN_OBJECT,
        Opcode.CHECK_CAST,
        Opcode.SGET,
        Opcode.SGET_OBJECT
    )
)
