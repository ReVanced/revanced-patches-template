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
object HideWatermarkSignature : MethodSignature(
    null, null, listOf("L", "L"), null ,null
)