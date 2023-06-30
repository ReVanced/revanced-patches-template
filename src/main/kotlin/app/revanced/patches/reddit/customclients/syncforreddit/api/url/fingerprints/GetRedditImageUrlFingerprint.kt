package app.revanced.patches.reddit.customclients.syncforreddit.api.url.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint

object GetRedditImageUrlFingerprint : MethodFingerprint(
    strings = listOf("preview.redd.it")
)
