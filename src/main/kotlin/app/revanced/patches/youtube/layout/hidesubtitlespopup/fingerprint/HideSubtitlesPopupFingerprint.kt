package app.revanced.patches.youtube.layout.hidesubtitlespopup.fingerprint

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.Opcode

object HideSubtitlesPopupFingerprint : MethodFingerprint(
        "V",
        AccessFlags.PUBLIC or AccessFlags.FINAL,
        opcodes =  listOf(
                Opcode.NEW_ARRAY,
                Opcode.INVOKE_STATIC,
                Opcode.MOVE_RESULT_OBJECT,
                Opcode.APUT_OBJECT,
                Opcode.CHECK_CAST,
                Opcode.CONST,
                Opcode.INVOKE_VIRTUAL,
                Opcode.MOVE_RESULT_OBJECT,
        ),
)