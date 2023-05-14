package app.revanced.patches.tiktok.misc.settings.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint

object SettingsOnViewCreatedFingerprint : MethodFingerprint(
    customFingerprint = { methodDef, _ ->
        methodDef.definingClass.endsWith("/SettingNewVersionFragment;") &&
                methodDef.name == "onViewCreated"
    }
)