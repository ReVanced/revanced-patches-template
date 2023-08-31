package app.revanced.patches.grindr.microg.fingerprints


import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint

object PrimeFingerprint : MethodFingerprint(
    strings = listOf("com.android.vending", "com.google.android.gms"),
    customFingerprint = { methodDef, _ ->
        methodDef.name.contains("startConnection") && !methodDef.name.contains("zzb")
    }
)