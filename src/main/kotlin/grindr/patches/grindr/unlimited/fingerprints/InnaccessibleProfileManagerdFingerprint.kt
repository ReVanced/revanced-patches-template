package app.revanced.patches.grindr.unlimited.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.annotation.FuzzyPatternScanMethod
import app.revanced.patcher.fingerprint.MethodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

object InnaccessibleProfileManagerdFingerprint : MethodFingerprint(
    "Z",
    opcodes = listOf(
        Opcode.INVOKE_VIRTUAL,
        Opcode.MOVE_RESULT,
        Opcode.XOR_INT_LIT8,
        Opcode.RETURN
    ),
    customFingerprint = { methodDef, _ ->
        methodDef.definingClass.contains("InaccessibleProfileManager")
    }
)