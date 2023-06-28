package app.revanced.patches.reddit.layout.disablescreenshotpopup.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint

object DisableScreenshotPopupFingerprint : MethodFingerprint(
    "V",
    parameters = listOf("Landroidx/compose/runtime/e;", "I"),
    customFingerprint = { methodDef, classDef ->
        classDef.type.endsWith("Lcom/reddit/sharing/screenshot/composables/ComposableSingletons\$ScreenshotTakenBannerKt\$lambda-1\$1;") &&
                methodDef.name == "invoke" && methodDef.parameters.size == 2
    }
)