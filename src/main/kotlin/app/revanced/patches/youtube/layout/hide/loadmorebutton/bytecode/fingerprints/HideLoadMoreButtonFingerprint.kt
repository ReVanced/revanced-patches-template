package app.revanced.patches.youtube.layout.hide.loadmorebutton.bytecode.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patches.youtube.layout.hide.loadmorebutton.resourcce.patch.HideLoadMoreButtonResourcePatch
import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.Opcode
import org.jf.dexlib2.iface.instruction.WideLiteralInstruction

object HideLoadMoreButtonFingerprint : MethodFingerprint(
    returnType = "V",
    access = AccessFlags.PUBLIC or AccessFlags.CONSTRUCTOR,
    parameters = listOf("L", "L", "L", "L"),
    opcodes = listOf(
        Opcode.INVOKE_DIRECT,
        Opcode.INVOKE_VIRTUAL,
        Opcode.IPUT_OBJECT,
        Opcode.IPUT_OBJECT,
        Opcode.IPUT_OBJECT,
        Opcode.INVOKE_VIRTUAL,
        Opcode.CONST,
        Opcode.CONST_4,
        Opcode.INVOKE_STATIC,
        Opcode.MOVE_RESULT_OBJECT
    ),
    customFingerprint = { methodDef ->
        methodDef.implementation?.instructions?.any { instruction ->
            instruction.opcode.ordinal == Opcode.CONST.ordinal &&
                    (instruction as? WideLiteralInstruction)?.wideLiteral == HideLoadMoreButtonResourcePatch.expandButtonDownId
        } == true
    }
)