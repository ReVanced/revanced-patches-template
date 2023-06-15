package app.revanced.patches.boostforreddit.api.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint

object GetClientIdFingerprint : MethodFingerprint(
    customFingerprint = { methodDef, classDef ->
        classDef.type.endsWith("Credentials;")
                && methodDef.name == "getClientId"
    }
)