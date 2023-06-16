package app.revanced.patches.syncforreddit.api.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint

object GetRedditUrlFingerprint : MethodFingerprint(
    strings = listOf("oauth.reddit.com")
)
