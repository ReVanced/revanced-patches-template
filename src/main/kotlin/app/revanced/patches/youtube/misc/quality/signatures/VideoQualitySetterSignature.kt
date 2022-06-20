package app.revanced.patches.youtube.misc.quality.signatures

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.signature.implementation.method.MethodSignature
import app.revanced.patcher.signature.implementation.method.annotation.DirectPatternScanMethod
import app.revanced.patcher.signature.implementation.method.annotation.MatchingMethod
import app.revanced.patches.youtube.misc.quality.annotations.DefaultVideoQualityCompatibility
import org.jf.dexlib2.Opcode

@Name("video-quality-setter-signature")
@MatchingMethod("Lkdy;", "b")
@DirectPatternScanMethod
@DefaultVideoQualityCompatibility
@Version("0.0.1")
object VideoQualitySetterSignature : MethodSignature(
    null, null, null, listOf(
        Opcode.IPUT_OBJECT, Opcode.RETURN
    )
)
