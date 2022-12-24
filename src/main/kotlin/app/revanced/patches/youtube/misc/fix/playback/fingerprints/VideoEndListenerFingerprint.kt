package app.revanced.patches.youtube.misc.fix.playback.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.Opcode

object VideoEndListenerFingerprint : MethodFingerprint(
    returnType = "Z",
    access = AccessFlags.PUBLIC or AccessFlags.FINAL,
    opcodes = listOf(
        Opcode.INVOKE_VIRTUAL_RANGE,
        Opcode.IGET_OBJECT,
        Opcode.IGET_OBJECT,
        Opcode.CHECK_CAST,
        Opcode.CONST_WIDE_32
    ),
    strings = listOf(
        "Attempting to seek during an ad"
    )
)