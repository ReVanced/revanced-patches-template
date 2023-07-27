package app.revanced.patches.instagram.patches.ads.timeline.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint

object MediaFingerprint : MethodFingerprint(
    strings = listOf("organic_media_updated_with_sponsored_info", " ad_id: ", "m_pk: ")
)
