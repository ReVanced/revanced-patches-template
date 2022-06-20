package app.revanced.patches.youtube.misc.quality.signatures

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.signature.implementation.method.MethodSignature
import app.revanced.patcher.signature.implementation.method.annotation.DirectPatternScanMethod
import app.revanced.patcher.signature.implementation.method.annotation.MatchingMethod
import app.revanced.patches.youtube.misc.quality.annotations.DefaultVideoQualityCompatibility

@Name("video-qualities-fragment-getter-signature")
@MatchingMethod("Lkdy;", "createVideoQualityFragment")
@DirectPatternScanMethod
@DefaultVideoQualityCompatibility
@Version("0.0.1")
object VideoQualitiesFragmentGetterSignature : MethodSignature(
    "V", null, null, null, listOf("VIDEO_QUALITIES_MENU_BOTTOM_SHEET_FRAGMENT")
)
