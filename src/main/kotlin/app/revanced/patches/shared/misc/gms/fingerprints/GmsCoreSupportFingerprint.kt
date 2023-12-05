package app.revanced.patches.shared.misc.gms.fingerprints

import app.revanced.patcher.fingerprint.MethodFingerprint

object GmsCoreSupportFingerprint : MethodFingerprint(
    customFingerprint = { _, classDef ->
        classDef.type.endsWith("GmsCoreSupport;")
    }
) {
    const val GET_GMS_CORE_VENDOR_METHOD_NAME = "getGmsCoreVendor"
}
