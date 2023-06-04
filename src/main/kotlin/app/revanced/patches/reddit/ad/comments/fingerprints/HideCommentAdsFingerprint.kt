package app.revanced.patches.reddit.ad.comments.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint

object HideCommentAdsFingerprint : MethodFingerprint(
    // This string was
    // "Error loading comment ads" in older versions
    // and
    // "Error loading comments page ad" in newer versions
    strings = listOf(
        "Error loading comment",
    ),
    customFingerprint = { _, classDef ->
        classDef.sourceFile == "RedditCommentsPageAdRepository.kt"
    },
)