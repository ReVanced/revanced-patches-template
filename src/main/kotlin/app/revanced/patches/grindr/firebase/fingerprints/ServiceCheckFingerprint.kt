package app.revanced.patches.grindr.microg.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import org.jf.dexlib2.AccessFlags

object ServiceCheckFingerprint : MethodFingerprint(
    returnType = "V",
    strings = listOf("Google Play Services not available"),
)