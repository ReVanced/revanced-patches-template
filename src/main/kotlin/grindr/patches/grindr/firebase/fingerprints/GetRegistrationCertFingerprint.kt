package app.revanced.patches.grindr.firebase.fingerprints

import app.revanced.patcher.fingerprint.MethodFingerprint

object GetRegistrationCertFingerprint : MethodFingerprint(
    "Ljava/lang/String;",
    strings = listOf(
        "FirebaseRemoteConfig",
        "Could not get fingerprint hash for package: ",
        "No such package: "
    )
)