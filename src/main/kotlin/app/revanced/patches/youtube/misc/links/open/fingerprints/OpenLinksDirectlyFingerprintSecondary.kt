package app.revanced.patches.youtube.misc.links.open.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.Opcode

object OpenLinksDirectlyFingerprintSecondary : MethodFingerprint(
    returnType = "Landroid/net/Uri",
    access = AccessFlags.PUBLIC or AccessFlags.STATIC,
    parameters = listOf("Ljava/lang/String"),
    opcodes = listOf(
        Opcode.INVOKE_STATIC,
        Opcode.MOVE_RESULT_OBJECT
    ),
    strings = listOf("://")
)
