package app.revanced.patches.unifiprotect.dialog.networkerror.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint

object HideNetworkErrorDialogMethodFingerprint : MethodFingerprint(
        customFingerprint = custom@{ methodDef, classDef ->
            if (!classDef.type.endsWith("Lcom/ubnt/common/connect/ConnectControllerBinder;")) return@custom false

            methodDef.name == "showVpnErrorDialog"
        }
)
