package app.revanced.patches.photomath.misc.unlockplus.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import org.jf.dexlib2.AccessFlags

object IsPlusUnlockedFingerprint : MethodFingerprint(
    returnType = "Z",
    accessFlags = AccessFlags.PUBLIC or AccessFlags.FINAL,
    strings = listOf(
        "genius"
    ),
    customFingerprint = {
        methodDef, _ -> methodDef.definingClass.endsWith("/User;")
    }
)