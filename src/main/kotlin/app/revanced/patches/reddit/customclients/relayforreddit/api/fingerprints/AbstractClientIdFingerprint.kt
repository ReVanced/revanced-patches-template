package app.revanced.patches.reddit.customclients.relayforreddit.api.fingerprints

import app.revanced.patcher.fingerprint.MethodFingerprint

internal abstract class AbstractClientIdFingerprint(string: String) : MethodFingerprint(
    strings = listOfNotNull("dj-xCIZQYiLbEg", string),
)