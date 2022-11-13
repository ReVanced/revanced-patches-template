package app.revanced.patches.twitch.ad.video.fingerprints

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.extensions.or

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patches.twitch.ad.audio.annotations.AudioAdsCompatibility
import org.jf.dexlib2.AccessFlags

@Name("check-ad-eligibility-fingerprint")

@AudioAdsCompatibility
@Version("0.0.1")
object CheckAdEligibilityLambdaFingerprint : MethodFingerprint(
    "Lio/reactivex/SingleSource;",
    AccessFlags.PRIVATE or AccessFlags.FINAL or AccessFlags.STATIC,
    listOf(
        "Ltv/twitch/android/shared/ads/models/AdRequestInfo;",
        "Ltv/twitch/android/shared/ads/eligibility/AdEligibilityFetcher;",
        "Ltv/twitch/android/util/Optional;"
    ),
    customFingerprint = { method ->
        method.definingClass.endsWith("AdEligibilityFetcher;") &&
                method.name.contains("shouldRequestAd")
    }
)