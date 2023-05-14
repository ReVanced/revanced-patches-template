package app.revanced.patches.candylinkvpn.fingereprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint

object IsPremiumPurchasedFingerprint : MethodFingerprint(
    customFingerprint = { methodDef ->
        methodDef.definingClass.endsWith("PreferenceProvider;") &&
                methodDef.name == "isPremiumPurchased"
    }
) {
}