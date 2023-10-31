package app.revanced.patches.idaustria.detection.root.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags

object AttestationSupportedCheckFingerprint : MethodFingerprint(
    "V",
    accessFlags = AccessFlags.PUBLIC.value,
    customFingerprint = { methodDef, _ ->
        methodDef.name == "attestationSupportCheck" &&
        methodDef.definingClass.endsWith("/DeviceIntegrityCheck;")
    }
)
