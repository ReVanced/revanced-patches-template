package app.revanced.patches.tiktok.misc.forcelogin.fingerprints

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patches.tiktok.misc.forcelogin.annotations.DisableLoginRequirementCompatibility

@Name("mandatory-login-service-fingerprint2")
@DisableLoginRequirementCompatibility
@Version("0.0.1")
object MandatoryLoginServiceFingerprint2 : MethodFingerprint(
    customFingerprint = { methodDef ->
        methodDef.definingClass.endsWith("/MandatoryLoginService;") &&
                methodDef.name == "shouldShowForcedLogin"
    }
)