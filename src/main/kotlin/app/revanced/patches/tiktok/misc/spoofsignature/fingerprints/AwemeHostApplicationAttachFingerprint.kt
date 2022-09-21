package app.revanced.patches.tiktok.misc.spoofsignature.fingerprints

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.fingerprint.method.annotation.MatchingMethod
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patches.tiktok.misc.spoofsignature.annotations.SpoofSignatureCompatibility

@Name("AwemeHostApplicationAttachFingerprint")
@MatchingMethod(
    "Lcom/ss/android/ugc/aweme/app/host/AwemeHostApplication;",
    "attachBaseContext",
)
@SpoofSignatureCompatibility
@Version("0.0.1")
object AwemeHostApplicationAttachFingerprint : MethodFingerprint(
    customFingerprint = { methodDef ->
        methodDef.definingClass.endsWith("/AwemeHostApplication;") &&
                methodDef.name.endsWith("attachBaseContext")
    }
)