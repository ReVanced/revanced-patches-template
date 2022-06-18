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

@Name("quality-preference-signature")
@MatchingMethod(
    "Lkdy", "a"
)
@FuzzyPatternScanMethod(2) // FIXME: Test this threshold and find the best value.
@QualityPreferenceCompatibility
@Version("0.0.1")
object QualityPreferenceSignature : MethodSignature(
    "V",
    AccessFlags.PUBLIC or AccessFlags.FINAL,
    listOf("L","I","I","Z","I"),
    listOf(
        Opcode.IGET_OBJECT, // opcodes are broken
        Opcode.IF_EQ,
        Opcode.IGET,
        Opcode.IF_NE,
        Opcode.GOTO,
        Opcode.IPUT,
        Opcode.CONST_4,
        Opcode.IF_NE,
        Opcode.NEW_INSTANCE,
        Opcode.CONST_4,
        Opcode.INVOKE_DIRECT,
        Opcode.GOTO,
        Opcode.NEW_INSTANCE,
        Opcode.CONST_4,
        Opcode.INVOKE_DIRECT,
        Opcode.IPUT_OBJECT,
        Opcode.IF_NE,
        Opcode.INVOKE_VIRTUAL,
        Opcode.MOVE_RESULT_OBJECT,
        Opcode.INVOKE_VIRTUAL,
    )
)