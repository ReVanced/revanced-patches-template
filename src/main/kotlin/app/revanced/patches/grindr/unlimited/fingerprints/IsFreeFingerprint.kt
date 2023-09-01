package app.revanced.patches.grindr.unlimited.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.annotation.FuzzyPatternScanMethod
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.Opcode

object IsFreeFingerprint : MethodFingerprint(
    "Z",
    opcodes = listOf(
        Opcode.INVOKE_DIRECT,
        Opcode.MOVE_RESULT,
        Opcode.RETURN
    ),
    customFingerprint = { methodDef, _ ->
        methodDef.name.contains("isUnlimited")
    }
    
)