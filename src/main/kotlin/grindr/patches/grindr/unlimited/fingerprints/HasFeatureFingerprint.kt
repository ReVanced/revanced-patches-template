package app.revanced.patches.grindr.unlimited.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.annotation.FuzzyPatternScanMethod
import app.revanced.patcher.fingerprint.MethodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode


object HasFeatureFingerprint : MethodFingerprint(
    "Z",
    parameters = listOf("Lcom/grindrapp/android/model/Feature;"),
    opcodes = listOf(
        Opcode.CONST_STRING,
        Opcode.INVOKE_STATIC, 
        Opcode.INVOKE_STATIC, 
        Opcode.MOVE_RESULT, 
        Opcode.IGET_OBJECT, 
        Opcode.CONST_4, 
        Opcode.IF_LEZ, 
        Opcode.NEW_INSTANCE, 
        Opcode.CONST_STRING, 
        Opcode.INVOKE_DIRECT, 
        Opcode.INVOKE_VIRTUAL, 
        Opcode.CONST_STRING, 
        Opcode.INVOKE_VIRTUAL 
    )
)