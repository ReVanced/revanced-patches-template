package app.revanced.patches.syncforreddit.api.url.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint

object GetRedditUrlFingerprint : MethodFingerprint(
    strings = listOf("oauth.reddit.com")
)
