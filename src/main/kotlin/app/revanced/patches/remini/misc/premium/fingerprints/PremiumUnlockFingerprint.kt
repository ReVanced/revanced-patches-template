package app.revanced.patches.remini.misc.premium.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import org.jf.dexlib2.Opcode

object PremiumUnlockFingerprint : MethodFingerprint(
    opcodes = listOf(
        Opcode.INVOKE_VIRTUAL,
        Opcode.MOVE_RESULT_OBJECT,
        Opcode.IGET_BOOLEAN,
        Opcode.IF_EQZ,
        Opcode.GOTO,
        Opcode.CONST_4
    ),
    customFingerprint = { it.definingClass == "Lna/k;" && it.name == "o" }
)