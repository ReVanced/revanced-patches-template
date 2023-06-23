package app.revanced.patches.syncforreddit.api.url.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint

object GetRedditImageUrlFingerprint : MethodFingerprint(
    strings = listOf("preview.redd.it")
)
