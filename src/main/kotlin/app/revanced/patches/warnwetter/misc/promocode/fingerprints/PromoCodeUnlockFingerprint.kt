package app.revanced.patches.warnwetter.misc.promocode.fingerprints

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patches.warnwetter.misc.promocode.annotations.PromoCodeUnlockCompatibility

@Name("promo-code-unlock-fingerprint")
@PromoCodeUnlockCompatibility
@Version("0.0.1")
object PromoCodeUnlockFingerprint : MethodFingerprint(
    customFingerprint = { methodDef ->
        methodDef.definingClass.endsWith("PromoTokenVerification;") && methodDef.name == "isValid"
    }
)