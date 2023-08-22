package app.revanced.patches.grindr.microg.fingerprints


import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint

object PrimeFingerprint : MethodFingerprint(
    strings = listOf("com.android.vending", "com.google.android.gms"),
    customFingerprint = { methodDef, _ ->
        println("Found class: ${methodDef.definingClass} with method: ${methodDef.name} with return type: ${methodDef.returnType}")
        methodDef.name.contains("startConnection")
    }
)