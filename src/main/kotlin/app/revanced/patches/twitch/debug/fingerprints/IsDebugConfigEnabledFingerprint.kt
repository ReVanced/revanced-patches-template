package app.revanced.patches.twitch.debug.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint

object IsDebugConfigEnabledFingerprint : MethodFingerprint(
    customFingerprint = { methodDef, _ ->
        methodDef.definingClass.endsWith("/BuildConfigUtil;") && methodDef.name == "isDebugConfigEnabled"
    }
)