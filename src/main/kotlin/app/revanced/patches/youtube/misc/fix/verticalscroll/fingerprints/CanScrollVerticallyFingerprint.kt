package app.revanced.patches.youtube.misc.fix.verticalscroll.fingerprints


import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import org.jf.dexlib2.Opcode

object CanScrollVerticallyFingerprint : MethodFingerprint(
    "Z",
    parameters = emptyList(),
    opcodes = listOf(
        Opcode.IGET_OBJECT,
        Opcode.INSTANCE_OF,
        Opcode.CONST_4,
        Opcode.IF_EQZ,
        Opcode.CHECK_CAST,
        Opcode.INVOKE_STATIC,
        Opcode.MOVE_RESULT,
        Opcode.RETURN,
        Opcode.INVOKE_VIRTUAL,
        Opcode.MOVE_RESULT,
    ),
    customFingerprint = { methodDef -> methodDef.definingClass.endsWith("SwipeRefreshLayout;") }
)