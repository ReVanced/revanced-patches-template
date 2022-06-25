package app.revanced.patches.youtube.layout.watermark.signatures

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.signature.implementation.method.MethodSignature
import app.revanced.patcher.signature.implementation.method.annotation.FuzzyPatternScanMethod
import app.revanced.patcher.signature.implementation.method.annotation.MatchingMethod
import app.revanced.patches.youtube.layout.watermark.annotations.HideWatermarkCompatibility

@Name("hide-watermark-signature")
@MatchingMethod(
    "Ljvy;", "<init>"
)
@FuzzyPatternScanMethod(3)
@HideWatermarkCompatibility
@Version("0.0.1")
object HideWatermarkParentSignature : MethodSignature(
    "L", null, null, null, listOf("player_overlay_in_video_programming"),
)
