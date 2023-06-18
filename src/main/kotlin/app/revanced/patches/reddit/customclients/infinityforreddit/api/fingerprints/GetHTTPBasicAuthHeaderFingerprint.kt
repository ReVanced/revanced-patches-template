package app.revanced.patches.reddit.customclients.infinityforreddit.api.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint

object GetHTTPBasicAuthHeaderFingerprint : MethodFingerprint(
    strings = listOf("NOe2iKrPPzwscA"),
    customFingerprint = custom@{ methodDef, classDef ->
        if (!classDef.type.endsWith("APIUtils;")) return@custom false

        methodDef.name == "getHttpBasicAuthHeader"
    }
)
