package app.revanced.patches.reddit.layout.disablescreenshotpopup.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import org.jf.dexlib2.Opcode

object DisableScreenshotPopupFingerprint : MethodFingerprint(
    "V",
    opcodes = listOf(
        Opcode.AND_INT_LIT8,
        Opcode.CONST_4,
        Opcode.IF_NE,
        Opcode.INVOKE_INTERFACE_RANGE,
        Opcode.MOVE_RESULT,
        Opcode.IF_NEZ,
        Opcode.GOTO,
        Opcode.INVOKE_INTERFACE_RANGE,
    ),
    customFingerprint = { methodDef, _ ->
        methodDef.name == "invoke"
    }
)