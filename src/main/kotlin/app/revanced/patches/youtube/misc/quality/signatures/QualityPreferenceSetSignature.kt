package app.revanced.patches.youtube.misc.quality.signatures

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.extensions.or
import app.revanced.patcher.signature.implementation.method.MethodSignature
import app.revanced.patcher.signature.implementation.method.annotation.FuzzyPatternScanMethod
import app.revanced.patcher.signature.implementation.method.annotation.MatchingMethod
import app.revanced.patches.youtube.misc.quality.annotations.QualityPreferenceCompatibility
import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.Opcode

@Name("quality-preference-set-signature")
@MatchingMethod(
    "Lkdy", "onItemClick"
)
@FuzzyPatternScanMethod(2) // FIXME: Test this threshold and find the best value.
@QualityPreferenceCompatibility
@Version("0.0.1")
object QualityPreferenceSetSignature : MethodSignature(
    "V",
    AccessFlags.PUBLIC or AccessFlags.FINAL,
    listOf("L","L","I","J"),
    listOf(
        Opcode.IGET_OBJECT,
        Opcode.MOVE_OBJECT,
        Opcode.MOVE_OBJECT,
        Opcode.MOVE,
        Opcode.MOVE_WIDE,
        Opcode.INVOKE_INTERFACE_RANGE,
        Opcode.RETURN_VOID
    )
)