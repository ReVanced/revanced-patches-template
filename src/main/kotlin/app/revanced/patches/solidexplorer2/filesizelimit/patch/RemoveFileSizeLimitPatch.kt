package app.revanced.patches.solidexplorer2.filesizelimit.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.extensions.InstructionExtensions.replaceInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.solidexplorer2.filesizelimit.annotations.RemoveFileSizeLimitCompatibility
import app.revanced.patches.solidexplorer2.filesizelimit.fingerprints.OnReadyFingerprint
import com.android.tools.smali.dexlib2.iface.instruction.ThreeRegisterInstruction

@Patch
@Name("Remove file size limit")
@Description("Allows opening files larger than 2 Mb in the text editor.")
@RemoveFileSizeLimitCompatibility
class RemoveFileSizeLimitPatch : BytecodePatch(
    listOf(OnReadyFingerprint)
) {
    override fun execute(context: BytecodeContext) {
        val result = OnReadyFingerprint.result!!
        val startIndex = result.scanResult.patternScanResult!!.startIndex
        val comparisonInstruction = result.mutableMethod.getInstruction<ThreeRegisterInstruction>(startIndex + 1)
        val comparisonRegister = comparisonInstruction.registerA

        result.mutableMethod.replaceInstruction(
            startIndex + 1,
            "const/4 v${comparisonRegister}, 0x0"
        )
    }
}
