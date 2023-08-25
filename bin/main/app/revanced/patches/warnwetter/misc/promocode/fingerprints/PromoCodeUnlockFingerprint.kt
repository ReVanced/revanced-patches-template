package app.revanced.patches.warnwetter.misc.promocode.fingerprints
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint

object PromoCodeUnlockFingerprint : MethodFingerprint(
    customFingerprint = { methodDef, _ ->
        methodDef.definingClass.endsWith("PromoTokenVerification;") && methodDef.name == "isValid"
    }
)