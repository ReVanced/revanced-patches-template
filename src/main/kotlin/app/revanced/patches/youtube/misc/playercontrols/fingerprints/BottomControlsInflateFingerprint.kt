package app.revanced.patches.youtube.misc.playercontrols.fingerprints


import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patches.youtube.misc.playercontrols.bytecode.patch.PlayerControlsBytecodePatch
import org.jf.dexlib2.Opcode
import org.jf.dexlib2.iface.instruction.WideLiteralInstruction

object BottomControlsInflateFingerprint : MethodFingerprint(
    opcodes = listOf(
        Opcode.CHECK_CAST,
        Opcode.INVOKE_VIRTUAL,
        Opcode.MOVE_RESULT_OBJECT
    ),
    customFingerprint = { methodDef, _ ->
        methodDef.implementation?.instructions?.any { instruction ->
            instruction.opcode.ordinal == Opcode.CONST.ordinal &&
            (instruction as? WideLiteralInstruction)?.wideLiteral == PlayerControlsBytecodePatch.bottomUiContainerResourceId
        } == true
    }
)