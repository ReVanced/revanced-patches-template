package app.revanced.patches.youtube.layout.comments.bytecode.fingerprints

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patches.youtube.layout.comments.annotations.CommentsCompatibility
import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.Opcode

@Name("live-chat-fullscreen-button-visibility-fingerprint")
@CommentsCompatibility
@Version("0.0.1")
object LiveChatFullscreenButtonVisibilityFingerprint : MethodFingerprint(
    "V", AccessFlags.PRIVATE or AccessFlags.FINAL, listOf("Z", "Z"), listOf(
        Opcode.IGET_OBJECT,
        Opcode.IGET_OBJECT,
        Opcode.INVOKE_INTERFACE,
        Opcode.IF_EQZ,
        Opcode.IF_EQZ,
        Opcode.IGET_WIDE,
        Opcode.GOTO,
        Opcode.IGET_WIDE,
        Opcode.CONST_WIDE_16,
    )
)