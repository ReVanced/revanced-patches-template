package app.revanced.patches.youtube.misc.fix.backtoexit.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import org.jf.dexlib2.Opcode

object RecyclerViewTopScrollingFingerprint : MethodFingerprint(
    opcodes = listOf(
        Opcode.INVOKE_INTERFACE,
        Opcode.MOVE_RESULT_OBJECT,
        Opcode.CHECK_CAST,
        Opcode.CONST_4,
        Opcode.INVOKE_VIRTUAL,
        Opcode.GOTO,
        Opcode.IGET_OBJECT,
    )
)