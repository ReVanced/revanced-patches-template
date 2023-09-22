package app.revanced.patches.youtube.layout.utils.navbarindexhook.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags

object SettingsActivityOnBackPressedFingerprint : MethodFingerprint(
    accessFlags = AccessFlags.PUBLIC or AccessFlags.FINAL,
    returnType = "V",
    customFingerprint = { methodDef, _ ->
        methodDef.definingClass.endsWith("/SettingsActivity;")
                && methodDef.name == "onBackPressed"
    }
)