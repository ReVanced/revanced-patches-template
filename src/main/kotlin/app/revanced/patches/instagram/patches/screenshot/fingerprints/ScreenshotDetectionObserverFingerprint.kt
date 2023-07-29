package app.revanced.patches.instagram.patches.screenshot.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint

object ScreenshotDetectionObserverFingerprint : MethodFingerprint(
    "V",
    strings = listOf(
        "screenshot_detector",
        "ig_android_story_screenshot_directory",
        "screenshot_directory_exists",
        "phone_model"
    )
)