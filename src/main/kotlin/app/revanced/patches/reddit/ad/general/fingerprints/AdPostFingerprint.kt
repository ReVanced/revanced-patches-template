package app.revanced.patches.reddit.ad.general.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint

object AdPostFingerprint : MethodFingerprint(
    "V",
    // "children" are present throughout multiple versions
    // "uxExperiences" might not work on all versions
    // Not sure how to refine the search better.
    strings = listOf(
        "children",
        "uxExperiences"
    ),
    customFingerprint = { methodDef, _ ->
        methodDef.definingClass.endsWith("Listing;")
    },
)