package app.revanced.patches.reddit.misc.disablescreenshotpopup.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import org.jf.dexlib2.Opcode

object DisableScreenshotPopupFingerprint : MethodFingerprint(
    opcodes = listOf(
        Opcode.AND_INT_LIT8,
        Opcode.CONST_4,
        Opcode.IF_NE
    ),
    customFingerprint = { methodDef, _ ->
        methodDef.name == "invoke"
    }
)