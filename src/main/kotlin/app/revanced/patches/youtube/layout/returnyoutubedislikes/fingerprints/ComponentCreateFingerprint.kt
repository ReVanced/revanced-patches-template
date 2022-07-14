package app.revanced.patches.youtube.layout.returnyoutubedislikes.fingerprints

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.fingerprint.method.annotation.FuzzyPatternScanMethod
import app.revanced.patcher.fingerprint.method.annotation.MatchingMethod
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patches.youtube.layout.returnyoutubedislikes.annotations.RYDCompatibility
import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.Opcode

@Name("text-component-create-fingerprint")
@MatchingMethod(
    "Lnkc;", "f"
)
@FuzzyPatternScanMethod(2)
@RYDCompatibility
@Version("0.0.1")
object ComponentCreateFingerprint : MethodFingerprint(
    "L",
    AccessFlags.STATIC.value,
    listOf("L", "L", "L", "L", "L", "L", "L", "L", "F", "F", "F", "L", "Z", "Z", "L", "L", "L", "L", "L"),
    listOf(
        Opcode.MOVE_OBJECT,
        Opcode.INVOKE_VIRTUAL_RANGE,
        Opcode.MOVE_RESULT_OBJECT,
        Opcode.CHECK_CAST,
        // combination with params is already unique enough
    )
)