package app.revanced.patches.youtube.layout.comments.bytecode.fingerprints

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patches.youtube.layout.comments.annotations.CommentsCompatibility
import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.Opcode

@Name("second-set-visibility-anchor-fingerprint")
@CommentsCompatibility
@Version("0.0.1")
object SecondSetVisibilityAnchor : MethodFingerprint(
    "V", AccessFlags.PUBLIC or AccessFlags.FINAL, opcodes = listOf(
        Opcode.IGET_OBJECT,
        Opcode.IGET,
        Opcode.INVOKE_VIRTUAL,
    )
)