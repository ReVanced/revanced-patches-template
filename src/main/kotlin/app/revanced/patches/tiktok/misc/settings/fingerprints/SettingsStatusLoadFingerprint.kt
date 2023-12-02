package app.revanced.patches.tiktok.misc.settings.fingerprints

import app.revanced.patcher.fingerprint.MethodFingerprint

internal object SettingsStatusLoadFingerprint : MethodFingerprint(
    customFingerprint = { methodDef, _ ->
        methodDef.definingClass.endsWith("Lapp/revanced/tiktok/settingsmenu/SettingsStatus;") &&
                methodDef.name == "load"
    }
)