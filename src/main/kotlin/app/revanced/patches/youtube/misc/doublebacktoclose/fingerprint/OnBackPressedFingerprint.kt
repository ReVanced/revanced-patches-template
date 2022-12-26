package app.revanced.patches.youtube.misc.doublebacktoclose.fingerprint

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import org.jf.dexlib2.Opcode

object OnBackPressedFingerprint : MethodFingerprint(
    opcodes = listOf(
        Opcode.RETURN_VOID
    ),
    customFingerprint = { methodDef ->
        methodDef.definingClass.endsWith("WatchWhileActivity;")
                && methodDef.name == "onBackPressed"
    }
)
