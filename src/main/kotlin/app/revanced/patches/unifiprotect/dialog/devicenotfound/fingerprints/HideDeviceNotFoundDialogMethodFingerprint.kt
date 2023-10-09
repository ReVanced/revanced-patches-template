package app.revanced.patches.unifiprotect.dialog.devicenotfound.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint

object HideDeviceNotFoundDialogMethodFingerprint : MethodFingerprint(
        customFingerprint = custom@{ methodDef, classDef ->
            if (!classDef.type.endsWith("Lcom/ubnt/common/connect/ConnectControllerBinder;")) return@custom false

            methodDef.name == "showLocalDeviceNotFoundDialog"
        }
)
