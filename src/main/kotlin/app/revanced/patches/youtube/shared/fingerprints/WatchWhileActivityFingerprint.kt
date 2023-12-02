package app.revanced.patches.youtube.shared.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.MethodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags

internal object WatchWhileActivityFingerprint : MethodFingerprint(
    accessFlags = AccessFlags.PUBLIC or AccessFlags.CONSTRUCTOR,
    parameters = listOf(),
    customFingerprint = { methodDef, _ ->
        methodDef.definingClass.endsWith("WatchWhileActivity;")
    }
)
