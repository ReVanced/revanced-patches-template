package app.revanced.patches.youtubevanced.ad.general.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.Opcode

object ContainsAdFingerprintPrimary : MethodFingerprint(
    returnType = "Z",
    parameters = listOf("L"),
    access = AccessFlags.STATIC or AccessFlags.PUBLIC,
    opcodes = listOf(
        Opcode.CONST_STRING,
        Opcode.INVOKE_INTERFACE,
        Opcode.CONST_STRING,
        Opcode.INVOKE_INTERFACE
    ),
    strings = listOf("ad_badge"),
    customFingerprint = { methodDef ->
        methodDef.name == "containsAd" && methodDef.definingClass.endsWith("LithoAdRemoval;")
    }
) {
}