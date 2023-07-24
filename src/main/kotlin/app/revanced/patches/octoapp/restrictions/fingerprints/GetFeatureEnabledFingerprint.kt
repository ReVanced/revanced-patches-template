package app.revanced.patches.octoapp.restrictions.fingereprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint

object GetFeatureEnabledFingerprint : MethodFingerprint(
    customFingerprint = custom@{ methodDef, classDef ->
        if (!classDef.type.endsWith("BillingManager;")) return@custom false

        methodDef.name == "isFeatureEnabled"
    }
)
