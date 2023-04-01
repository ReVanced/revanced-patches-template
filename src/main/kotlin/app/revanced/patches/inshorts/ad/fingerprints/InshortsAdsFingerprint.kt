package app.revanced.patches.inshorts.ad.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import org.jf.dexlib2.Opcode

object InshortsAdsFingerprint : MethodFingerprint(
    "V",
    strings = listOf("GoogleAdLoader","exception in requestAd"),
    opcodes = listOf(
        Opcode.INVOKE_VIRTUAL_RANGE,
        Opcode.GOTO,
        Opcode.MOVE_EXCEPTION,
        Opcode.CONST_STRING,
        Opcode.CONST_STRING,
        Opcode.INVOKE_STATIC,
     ),
)