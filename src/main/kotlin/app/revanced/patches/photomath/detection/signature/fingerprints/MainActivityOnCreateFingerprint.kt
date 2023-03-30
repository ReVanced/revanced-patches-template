package app.revanced.patches.photomath.detection.signature.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import org.jf.dexlib2.AccessFlags

// Used for versions from 8.21.0 onwards
object MainActivityOnCreateFingerprint : MethodFingerprint(
    returnType = "V",
    access = AccessFlags.PUBLIC or AccessFlags.FINAL,
    customFingerprint = { methodDef ->
        methodDef.definingClass == "Lcom/microblink/photomath/main/activity/LauncherActivity;" && methodDef.name == "onCreate"
    }
)