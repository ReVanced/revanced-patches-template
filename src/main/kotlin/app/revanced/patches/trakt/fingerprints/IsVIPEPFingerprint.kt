package app.revanced.patches.trakt.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint

object IsVIPEPFingerprint : MethodFingerprint(
    customFingerprint = { methodDef, _ ->
        methodDef.definingClass.endsWith("RealmUserSettings;") && methodDef.name == "isVIPEP"
    }
) {
}