package app.revanced.patches.iconpackstudio.misc.pro.fingerprints

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patches.iconpackstudio.misc.pro.annotations.UnlockProCompatibility

@Name("check-pro-fingerprint")
@UnlockProCompatibility
@Version("0.0.1")
object CheckProFingerprint : MethodFingerprint(
    customFingerprint = { methodDef ->
        methodDef.definingClass.endsWith("IPSPurchaseRepository;") && methodDef.name == "b"
    }
)
