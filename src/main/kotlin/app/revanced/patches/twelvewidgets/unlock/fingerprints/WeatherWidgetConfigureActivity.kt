package app.revanced.patches.twelvewidgets.unlock.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint

object WeatherWidgetConfigureActivity : MethodFingerprint(
    "L",
    customFingerprint = { methodDef ->
        methodDef.definingClass.endsWith("/WeatherWidgetConfigureActivity;") && methodDef.name == "getAddButton"
    }
)
