package app.revanced.patches.music.audio.codecs.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.annotation.FuzzyPatternScanMethod
import app.revanced.patcher.fingerprint.MethodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode


@FuzzyPatternScanMethod(2) // FIXME: Test this threshold and find the best value.
internal object CodecsLockFingerprint : MethodFingerprint(
    "L", AccessFlags.PUBLIC or AccessFlags.STATIC, opcodes = listOf(
        Opcode.INVOKE_DIRECT,
        Opcode.INVOKE_VIRTUAL,
        Opcode.MOVE_RESULT,
        Opcode.IF_NEZ,
        Opcode.INVOKE_VIRTUAL,
        Opcode.MOVE_RESULT,
        Opcode.IF_NEZ,
        Opcode.SGET_OBJECT,
        Opcode.IGET_OBJECT,
        Opcode.INVOKE_STATIC,
        Opcode.MOVE_RESULT_OBJECT,
        Opcode.INVOKE_INTERFACE,
        Opcode.INVOKE_VIRTUAL,
        Opcode.RETURN_OBJECT
    ),
    strings = listOf("eac3_supported")
)
