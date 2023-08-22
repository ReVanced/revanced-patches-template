package app.revanced.patches.grindr.microg.fingerprints


import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint

object CastContextFetchFingerprint : MethodFingerprint(
    strings = listOf("Error fetching CastContext.")
)