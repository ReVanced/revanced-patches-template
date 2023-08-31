package app.revanced.patches.grindr.microg.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import org.jf.dexlib2.AccessFlags

object GooglePlayServicesManifestValueExceptionFingerprint : MethodFingerprint(
    returnType = "I",
    accessFlags = AccessFlags.PUBLIC or AccessFlags.STATIC,
    strings = listOf("The Google Play services resources were not found. Check your project configuration to ensure that the resources are included."),
    customFingerprint = { methodDef, _ ->
        methodDef.name.contains("isGooglePlayServicesAvailable")
    }
)