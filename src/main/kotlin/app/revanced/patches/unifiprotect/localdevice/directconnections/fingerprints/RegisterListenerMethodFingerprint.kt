package app.revanced.patches.unifiprotect.localdevice.directconnections.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags

object RegisterListenerMethodFingerprint : MethodFingerprint(
    accessFlags = AccessFlags.PUBLIC or AccessFlags.FINAL,
    customFingerprint = custom@{ methodDef, classDef ->
        if (!classDef.type.endsWith("Lcom/ubnt/common/service/discovery/DeviceDiscoveryService;")) return@custom false

        methodDef.name == "registerListener"
    }
)