package app.revanced.patches.twitch.debug.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint

object ShouldShowDebugOptionsFingerprint : MethodFingerprint(
    customFingerprint = { methodDef, _ ->
        methodDef.definingClass.endsWith("/BuildConfigUtil;") && methodDef.name == "shouldShowDebugOptions"
    }
)