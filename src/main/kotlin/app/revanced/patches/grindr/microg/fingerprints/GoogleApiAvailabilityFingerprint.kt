package app.revanced.patches.grindr.microg.fingerprints


import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint

object GoogleApiAvailabilityFingerprint : MethodFingerprint(
    customFingerprint = { methodDef, _ ->
        methodDef.name.contains("getInstance")
    }
)