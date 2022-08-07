package app.revanced.patches.warnwetter.misc.firebasegetcert.fingerprints

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.fingerprint.method.annotation.MatchingMethod
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patches.warnwetter.misc.firebasegetcert.annotations.FirebaseGetCertPatchCompatibility

@Name("registration-app-certificate-fingerprint")
@MatchingMethod(
    "Lcom/google/firebase/remoteconfig/internal/ConfigFetchHttpClient;", "f"
)
@FirebaseGetCertPatchCompatibility
@Version("0.0.1")
object GetReqistrationCertFingerprint : MethodFingerprint(
    "Ljava/lang/String;",
    null,
    null,
    null,
    listOf(
        "FirebaseRemoteConfig",
        "Could not get fingerprint hash for package: ",
        "No such package: "
    )
)