package app.revanced.patches.backdrops.misc.pro.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint

object ProUnlockFingerprint : MethodFingerprint(
    customFingerprint = { it.definingClass == "Lcom/backdrops/wallpapers/data/local/DatabaseHandlerIAB;" && it.name == "lambda\$existPurchase\$0" }
)