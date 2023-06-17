package app.revanced.patches.reddit.layout.disablescreenshotpopup.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint

object DisableScreenshotPopupFingerprint : MethodFingerprint(
    "V",
    parameters = listOf("Landroidx/compose/runtime/e;", "I"),
    customFingerprint = { methodDef, classDef ->
        classDef.type.endsWith("ScreenshotTakenBannerKt\$lambda-1\$1;") &&
                methodDef.name == "invoke"
    }
)