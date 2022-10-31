package app.revanced.patches.youtube.layout.hidecrowdfundingbox.bytecode.fingerprints

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patches.youtube.layout.hidecrowdfundingbox.annotations.CrowdfundingBoxCompatibility
import org.jf.dexlib2.Opcode

@Name("crowdfunding-box-view-parent-fingerprint")
@CrowdfundingBoxCompatibility
@Version("0.0.1")
object CrowdfundingBoxParentFingerprint : MethodFingerprint(
    opcodes = listOf(
        Opcode.CONST_4,
        Opcode.CONST,
        Opcode.INVOKE_VIRTUAL,
        Opcode.MOVE_RESULT_OBJECT,
        Opcode.IPUT_OBJECT,
        Opcode.CONST,
    )
)