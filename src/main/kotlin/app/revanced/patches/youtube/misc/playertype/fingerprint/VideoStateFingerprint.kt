package app.revanced.patches.youtube.misc.playertype.fingerprint

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.Opcode

object VideoStateFingerprint : MethodFingerprint(
    access = AccessFlags.PUBLIC or AccessFlags.FINAL,
    returnType = "V",
    parameters = listOf("Lcom/google/android/libraries/youtube/player/features/overlay/controls/ControlsState;"),
    opcodes = listOf(
        Opcode.IGET_OBJECT,
        Opcode.IGET_OBJECT,
        Opcode.IGET_OBJECT,
        Opcode.INVOKE_VIRTUAL,
        Opcode.MOVE_RESULT,
        Opcode.IF_NEZ,
        Opcode.INVOKE_VIRTUAL,
        Opcode.IGET_OBJECT,
    ),
    customFingerprint = { methodDef, _ ->
        methodDef.definingClass.endsWith("YouTubeControlsOverlay;")
    }
)