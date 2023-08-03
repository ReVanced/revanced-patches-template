package app.revanced.patches.lightroom.misc.pro.fingerprint

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.Opcode

object UnlockPremiumFingerprint : MethodFingerprint(
    returnType = "Z",
    accessFlags = AccessFlags.PRIVATE or AccessFlags.FINAL,
    strings = listOf("isPurchaseDoneRecently = true, access platform profile present? = "),
    opcodes = listOf(
        Opcode.SGET_OBJECT,
        Opcode.CONST_4,
        Opcode.CONST_4,
        Opcode.CONST_4,
    )
)
