package app.revanced.patches.unifiprotect.localdevice.bypasswifichecks.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint

object IsWifiMethodFingerprint : MethodFingerprint(
    "Z",
    customFingerprint = custom@{ methodDef, classDef ->
        if (!classDef.type.endsWith("Lcom/ubnt/util/NetworkUtils;")) return@custom false

        methodDef.name == "isWifi"
    }
)