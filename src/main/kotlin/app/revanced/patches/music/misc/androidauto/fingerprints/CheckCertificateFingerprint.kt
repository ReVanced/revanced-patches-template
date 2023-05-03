package app.revanced.patches.music.misc.androidauto.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint


object CheckCertificateFingerprint : MethodFingerprint(
    "Z",
    strings = listOf("No match") // Unique in combination with boolean return type
)