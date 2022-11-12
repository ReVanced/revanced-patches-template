package app.revanced.patches.youtube.misc.video.speed.default.fingerprints

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.annotation.YouTubeCompatibility
import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.Opcode

@Name("video-user-speed-change-fingerprint")
@YouTubeCompatibility
@Version("0.0.1")
object VideoUserSpeedChangeFingerprint : MethodFingerprint(
    "V",
    AccessFlags.PUBLIC or AccessFlags.FINAL,
    listOf("L","L","I","J"),
    listOf(
        Opcode.IGET_OBJECT,
        Opcode.IF_EQZ,
        Opcode.IF_EQZ,
        Opcode.IGET,
        Opcode.CHECK_CAST,
        Opcode.IGET_OBJECT,
        Opcode.INVOKE_VIRTUAL,
        Opcode.IGET_OBJECT,
        Opcode.INVOKE_STATIC,
        Opcode.MOVE_RESULT_OBJECT,
        Opcode.INVOKE_VIRTUAL,
        Opcode.INVOKE_VIRTUAL,
        Opcode.RETURN_VOID
    )
)
