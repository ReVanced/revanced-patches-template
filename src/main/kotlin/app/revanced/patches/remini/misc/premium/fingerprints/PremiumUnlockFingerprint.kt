package app.revanced.patches.remini.misc.premium.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import org.jf.dexlib2.Opcode

object PremiumUnlockFingerprint : MethodFingerprint(
    opcodes = listOf(
        Opcode.IGET_BOOLEAN,
        Opcode.IF_NEZ,
        Opcode.INVOKE_VIRTUAL,
        Opcode.MOVE_RESULT_OBJECT,
        Opcode.IGET_BOOLEAN,
        Opcode.IF_EQZ,
        Opcode.GOTO,
        Opcode.CONST_4
    ),
    customFingerprint = { it.parameterTypes[0] == "Ljava/lang/Object;" },
    returnType = "Ljava/lang/Object;"
)