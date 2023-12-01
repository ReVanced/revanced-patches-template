package app.revanced.patches.reddit.customclients.infinityforreddit.api.fingerprints

internal object GetHttpBasicAuthHeaderFingerprint : AbstractClientIdFingerprint(additionalStrings = arrayOf("Authorization"))