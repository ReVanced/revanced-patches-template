package app.revanced.patches.tiktok.ad.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint

object ConvertHelpFeedItemListFingerprint : MethodFingerprint(
    customFingerprint = { methodDef, _ ->
        methodDef.definingClass.endsWith("/ConvertHelp;") &&
                methodDef.name.endsWith("${'$'}FeedItemList")
    }
)