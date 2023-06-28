package app.revanced.patches.youtube.video.oldqualitylayout.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.util.patch.LiteralValueFingerprint
import app.revanced.patches.youtube.video.oldqualitylayout.patch.OldQualityLayoutResourcePatch
import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.Opcode

object QualityMenuViewInflateFingerprint : LiteralValueFingerprint(
    accessFlags = AccessFlags.PUBLIC or AccessFlags.FINAL,
    parameters = listOf("L", "L", "L"),
    returnType = "L",
    opcodes = listOf(
        Opcode.INVOKE_SUPER,
        Opcode.CONST,
        Opcode.CONST_4,
        Opcode.INVOKE_VIRTUAL,
        Opcode.MOVE_RESULT_OBJECT,
        Opcode.CONST,
        Opcode.INVOKE_VIRTUAL,
        Opcode.MOVE_RESULT_OBJECT,
        Opcode.CONST_16,
        Opcode.INVOKE_VIRTUAL,
        Opcode.CONST,
        Opcode.INVOKE_VIRTUAL,
        Opcode.MOVE_RESULT_OBJECT,
        Opcode.CHECK_CAST
    ),
    literal = OldQualityLayoutResourcePatch.videoQualityBottomSheetListFragmentTitle
)