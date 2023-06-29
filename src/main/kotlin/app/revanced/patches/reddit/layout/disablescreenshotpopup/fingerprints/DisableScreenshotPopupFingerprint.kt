package app.revanced.patches.reddit.layout.disablescreenshotpopup.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint

object DisableScreenshotPopupFingerprint : MethodFingerprint(
    "V",
    parameters = listOf("Landroidx/compose/runtime/", "I"),
    customFingerprint = custom@{ methodDef, classDef ->
        if (!classDef.type.endsWith("Lcom/reddit/sharing/screenshot/composables/ComposableSingletons\$ScreenshotTakenBannerKt\$lambda-1\$1;")) return@custom false

        methodDef.name == "invoke"
    }
)