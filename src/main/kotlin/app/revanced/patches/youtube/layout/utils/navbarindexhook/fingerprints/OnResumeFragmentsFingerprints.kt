package app.revanced.patches.youtube.layout.utils.navbarindexhook.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint

object OnResumeFragmentsFingerprints: MethodFingerprint(
    customFingerprint = { methodDef, _ ->
        methodDef.definingClass.endsWith("WatchWhileActivity;")
                && methodDef.name == "onResumeFragments"
    }
)