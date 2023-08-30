package app.revanced.patches.grindr.microg.fingerprints


import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint

object GoogleApiAvailabilityFingerprint : MethodFingerprint(
    customFingerprint = { methodDef, _ ->
        println("Found class: ${methodDef.definingClass} with method: ${methodDef.name} with return type: ${methodDef.returnType}")
        methodDef.name.contains("getInstance")
    }
)