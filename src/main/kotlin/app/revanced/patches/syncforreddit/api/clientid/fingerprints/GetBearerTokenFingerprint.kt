package app.revanced.patches.syncforreddit.api.clientid.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint

object GetBearerTokenFingerprint : MethodFingerprint(
    strings = listOf("Basic")
)