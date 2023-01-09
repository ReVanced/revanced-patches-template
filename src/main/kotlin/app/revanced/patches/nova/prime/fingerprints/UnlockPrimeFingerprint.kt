package app.revanced.patches.nova.prime.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint

object UnlockPrimeFingerprint : MethodFingerprint(
    "V",
    parameters = listOf("L"),
    strings = listOf("ro.razer.internal.api")
)