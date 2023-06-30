package app.revanced.patches.reddit.customclients.syncforreddit.api.url.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint

object GetRedditUrlFingerprint : MethodFingerprint(
    strings = listOf("reddit.com")
)
