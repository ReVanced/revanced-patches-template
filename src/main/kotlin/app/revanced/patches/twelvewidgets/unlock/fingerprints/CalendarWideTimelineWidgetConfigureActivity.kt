package app.revanced.patches.twelvewidgets.unlock.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint

object CalendarWideTimelineWidgetConfigureActivity : MethodFingerprint(
    "L",
    customFingerprint = { methodDef ->
        methodDef.definingClass.endsWith("/CalendarWideTimelineWidgetConfigureActivity;") && methodDef.name == "getAddButton"
    }
)
