package app.revanced.patches.youtube.layout.sponsorblock.fingerprints

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.fingerprint.method.annotation.DirectPatternScanMethod
import app.revanced.patcher.fingerprint.method.annotation.MatchingMethod
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patches.youtube.layout.sponsorblock.annotations.SponsorBlockCompatibility

@Name("create-video-player-seekbar-fingerprint")
@MatchingMethod(
    "Lfcm;", "onDraw"
)
@DirectPatternScanMethod
@SponsorBlockCompatibility
@Version("0.0.1")
object CreateVideoPlayerSeekbarFingerprint : MethodFingerprint(
    "V", null, null,
    null,
    listOf("timed_markers_width")
)