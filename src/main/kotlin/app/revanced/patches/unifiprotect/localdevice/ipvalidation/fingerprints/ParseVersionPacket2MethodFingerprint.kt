package app.revanced.patches.unifiprotect.localdevice.ipvalidation.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import com.android.tools.smali.dexlib2.Opcode

object ParseVersionPacket2MethodFingerprint : MethodFingerprint(
    opcodes = listOf(
        Opcode.INVOKE_VIRTUAL,
        Opcode.MOVE_RESULT_OBJECT,
        Opcode.NEW_INSTANCE,
        Opcode.INVOKE_DIRECT,
        Opcode.IPUT_OBJECT,
        Opcode.GOTO,
    ),
    customFingerprint = custom@{ methodDef, classDef ->
        if (!classDef.type.endsWith("Lcom/ubnt/common/service/discovery/DiscoveryService;")) return@custom false

        methodDef.name == "parseVersion1Packet"
    }
)