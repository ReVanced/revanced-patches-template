package app.revanced.patches.grindr.microg.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint

object GetPackageNameFingerprint : MethodFingerprint(
    "Ljava/lang/String;",
    customFingerprint = { methodDef, _ ->
        methodDef.name.contains("getPackageName")
    }
)