package app.revanced.patches.urbandroid.sleep.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint

object IsTrialFingerprint : MethodFingerprint(
    "Z",
    customFingerprint = { it.name == "isTrial" }
)