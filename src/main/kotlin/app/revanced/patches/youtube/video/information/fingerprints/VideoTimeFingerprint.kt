package app.revanced.patches.youtube.video.information.fingerprints


import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint

object VideoTimeFingerprint : MethodFingerprint(
    strings = listOf("MedialibPlayerTimeInfo{currentPositionMillis=")
)