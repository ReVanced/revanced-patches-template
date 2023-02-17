package app.revanced.patches.twelvewidgets.unlock.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint

object CalendarWideDayEventsWidgetUnlockFingerprint : MethodFingerprint(
    "L",
    customFingerprint = { methodDef ->
        methodDef.definingClass.endsWith("/CalendarWideDayEventsWidgetConfigureActivity;") && methodDef.name == "getAddButton"
    }
)
