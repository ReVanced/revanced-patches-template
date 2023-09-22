package app.revanced.patches.cieid.restrictions.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint

object RootCheckerFingerprint : MethodFingerprint(
    customFingerprint = { methodDef, _ ->
        methodDef.definingClass == "Lit/ipzs/cieid/BaseActivity;" && methodDef.name == "onResume"
    }
)