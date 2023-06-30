package app.revanced.patches.reddit.customclients.syncforreddit.api.clientid.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint

object GetAuthorizationStringFingerprint : MethodFingerprint(
    strings = listOf("authorize.compact?client_id")
)