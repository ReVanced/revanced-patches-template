package app.revanced.patches.youtube.misc.integrations.fingerprints

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patcher.fingerprint.method.annotation.DirectPatternScanMethod
import app.revanced.patcher.fingerprint.method.annotation.MatchingMethod
import app.revanced.patches.youtube.misc.integrations.annotations.IntegrationsCompatibility

@Name("service-parent-fingerprint")
@MatchingMethod(
    "YoutubeService", "onBind"
)
@DirectPatternScanMethod
@IntegrationsCompatibility
@Version("0.0.1")
object ServiceParentFingerprint : MethodFingerprint(
    null, null, null, null,
    listOf("com.google.android.youtube.api.service.START")
)