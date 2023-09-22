package app.revanced.patches.youtube.shared.fingerprints


import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint

object SeekbarOnDrawFingerprint : MethodFingerprint(
    customFingerprint = { methodDef, _ -> methodDef.name == "onDraw" }
)