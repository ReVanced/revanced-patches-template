package app.revanced.patches.twelvewidgets.unlock.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint

object AgendaDaysWidgetUnlockFingerprint : MethodFingerprint(
    "L",
    customFingerprint = { methodDef ->
        methodDef.definingClass.endsWith("/AgendaDaysWidgetConfigureActivity;") && methodDef.name == "getAddButton"
    }
)
