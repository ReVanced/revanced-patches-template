package app.revanced.patches.unifiprotect.localdevice.ipvalidation.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import com.android.tools.smali.dexlib2.Opcode

object IPValidationMethodFingerprint : MethodFingerprint(
    opcodes = listOf(
        Opcode.INVOKE_VIRTUAL_RANGE,
        Opcode.MOVE_RESULT_OBJECT,
        Opcode.IPUT_OBJECT,
        Opcode.MOVE,
        Opcode.IF_GE,
        Opcode.ADD_INT_LIT8,
        Opcode.AGET_BYTE,
        Opcode.INVOKE_STATIC,
        Opcode.ADD_INT_2ADDR
    ),
    customFingerprint = custom@{ methodDef, classDef ->
        if (!classDef.type.endsWith("Lcom/ubnt/common/service/discovery/DiscoveryService;")) return@custom false

        methodDef.name == "parseVersion1Packet"
    }
)