package app.revanced.patches.grindr.microg.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import org.jf.dexlib2.AccessFlags

object OnChangedFingerprint : MethodFingerprint(
    returnType = "V",
    strings = listOf("account_verify_required_google", "no_google_play_service"),
    customFingerprint = { methodDef, _ ->
        methodDef.name.contains("onChanged")
    }
)