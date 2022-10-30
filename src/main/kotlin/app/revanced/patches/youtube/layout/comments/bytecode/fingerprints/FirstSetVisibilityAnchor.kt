package app.revanced.patches.youtube.layout.comments.bytecode.fingerprints

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patches.youtube.layout.comments.annotations.CommentsCompatibility
import org.jf.dexlib2.Opcode

@Name("first-set-visibility-anchor-fingerprint")
@CommentsCompatibility
@Version("0.0.1")
object FirstSetVisibilityAnchor : MethodFingerprint(
    opcodes = listOf(
        Opcode.INVOKE_VIRTUAL,
        Opcode.CONST_4,
        Opcode.INVOKE_DIRECT,
        Opcode.IGET_OBJECT,
        Opcode.IGET_OBJECT,
        Opcode.NEW_INSTANCE,
        Opcode.INVOKE_DIRECT,
        Opcode.INVOKE_INTERFACE,
        Opcode.RETURN_VOID,
        Opcode.IGET_WIDE,
        Opcode.IGET_OBJECT,
        Opcode.INVOKE_VIRTUAL,
    )
)