package app.revanced.patches.reddit.customclients.baconreader.api.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint

object RequestTokenFingerprint : MethodFingerprint(
    strings = listOf("zACVn0dSFGdWqQ"),
    customFingerprint = custom@{ _, classDef ->
       classDef.type.endsWith("RedditOAuth;")
    }
)