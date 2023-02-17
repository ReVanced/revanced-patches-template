package app.revanced.patches.twelvewidgets.unlock.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint

object CalendarBigWidgetUnlockFingerprint : MethodFingerprint(
    "L",
    customFingerprint = { methodDef ->
        methodDef.definingClass.endsWith("/CalendarBigWidgetConfigureActivity;") && methodDef.name == "getAddButton"
    }
)
