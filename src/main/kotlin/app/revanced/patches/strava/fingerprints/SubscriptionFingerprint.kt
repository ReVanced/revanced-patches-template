package app.revanced.patches.strava.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import org.jf.dexlib2.Opcode

object SubscriptionFingerprint : MethodFingerprint(
    returnType = "Z",
    opcodes = listOf(Opcode.IGET_BOOLEAN),
    customFingerprint = { methodDef, classDef ->
        classDef.type.endsWith("SubscriptionDetailResponse;") && methodDef.name == "getSubscribed"
    }
)
