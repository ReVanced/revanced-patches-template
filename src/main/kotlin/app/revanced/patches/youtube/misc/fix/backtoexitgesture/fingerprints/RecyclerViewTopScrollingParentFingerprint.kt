package app.revanced.patches.youtube.misc.fix.backtoexitgesture.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.Opcode

object RecyclerViewTopScrollingParentFingerprint : MethodFingerprint(
    accessFlags = AccessFlags.PUBLIC or AccessFlags.CONSTRUCTOR,
    parameters = listOf("L", "L", "L", "L"),
    opcodes = listOf(
        Opcode.INVOKE_DIRECT,
        Opcode.IPUT_OBJECT,
        Opcode.IPUT_OBJECT,
        Opcode.IPUT_OBJECT,
        Opcode.IPUT_OBJECT,
        Opcode.CONST_16,
        Opcode.INVOKE_VIRTUAL,
        Opcode.NEW_INSTANCE
    ),
    customFingerprint = { methodDef, _ ->
        methodDef.name == "<init>"
    }
)