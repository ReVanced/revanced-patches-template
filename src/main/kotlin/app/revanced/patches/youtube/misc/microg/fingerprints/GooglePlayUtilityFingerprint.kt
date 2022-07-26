package app.revanced.patches.youtube.misc.microg.fingerprints

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.annotation.FuzzyPatternScanMethod
import app.revanced.patcher.fingerprint.method.annotation.MatchingMethod
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patches.youtube.misc.microg.annotations.MicroGPatchCompatibility
import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.Opcode

@Name("google-play-utility-fingerprint")
@MatchingMethod(
    "Lmco;", "b"
)
@FuzzyPatternScanMethod(2) // FIXME: Test this threshold and find the best value.
@MicroGPatchCompatibility
@Version("0.0.1")
object GooglePlayUtilityFingerprint : MethodFingerprint(
    "I", AccessFlags.PUBLIC or AccessFlags.STATIC, listOf("L", "L"), listOf(
        Opcode.MOVE_RESULT_OBJECT,
        Opcode.INVOKE_STATIC,
        Opcode.GOTO,
        Opcode.IGET_BOOLEAN,
        Opcode.IF_NEZ,
        Opcode.CONST_4,
        Opcode.GOTO,
        Opcode.RETURN,
        Opcode.CONST_STRING,
        Opcode.INVOKE_STATIC,
        Opcode.MOVE_RESULT_OBJECT,
        Opcode.CONST_STRING,
        Opcode.INVOKE_VIRTUAL,
        Opcode.MOVE_RESULT_OBJECT,
        Opcode.INVOKE_STATIC,
        Opcode.RETURN
    ), listOf("This should never happen.", "MetadataValueReader", "com.google.android.gms")
)
