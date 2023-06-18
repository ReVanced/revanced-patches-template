package app.revanced.patches.reddit.customclients.infinityforreddit.api.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint

object LoginActivityOnCreateFingerprint : MethodFingerprint(
    strings = listOf("NOe2iKrPPzwscA"),
    customFingerprint = custom@{ methodDef, classDef ->
        if (!classDef.type.endsWith("LoginActivity;")) return@custom false

        methodDef.name == "onCreate"
    }
)
