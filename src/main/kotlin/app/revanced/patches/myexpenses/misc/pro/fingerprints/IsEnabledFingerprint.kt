package app.revanced.patches.myexpenses.misc.pro.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint

object GetTypeFingerprint : MethodFingerprint(
    "Z",
    strings = listOf("feature", "feature.licenceStatus"),
    customFingerprint = { methodDef -> methodDef.definingClass.endsWith("Liv/l;") }
)
