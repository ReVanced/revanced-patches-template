package app.revanced.patches.grindr.microg.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint

object OpenHttpURLConnectionFingerprint : MethodFingerprint(
    returnType = "Ljava/net/HttpURLConnection",
    strings = listOf("Failed to get heartbeats header", "ContentValues", "Content-Type", "application/json", "Accept"),
)