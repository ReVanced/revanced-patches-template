package app.revanced.patches.unifiprotect.localdevice.ipvalidation

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.extensions.InstructionExtensions.removeInstructions
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchException
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patches.unifiprotect.localdevice.ipvalidation.fingerprints.IPValidation2MethodFingerprint
import app.revanced.patches.unifiprotect.localdevice.ipvalidation.fingerprints.IPValidationMethodFingerprint
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction11x
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction35c

@Patch(
    name = "IP Validation",
    compatiblePackages = [CompatiblePackage("com.ubnt.unifi.protect")],
)

object IPValidationPatch : BytecodePatch(
    setOf(
        IPValidationMethodFingerprint,
        IPValidation2MethodFingerprint
    )
) {
    override fun execute(context: BytecodeContext) {
        val result = IPValidationMethodFingerprint.result ?: throw PatchException("Could not find method to patch")

        val index =
            result.scanResult.patternScanResult?.startIndex ?: throw PatchException("Could not find pattern to patch")

        result.mutableMethod.removeInstructions(index, 3)

        val result2 = IPValidation2MethodFingerprint.result ?: throw PatchException("Could not find method to patch")
        val patternScanResult = result2.scanResult.patternScanResult

        val startIndex = patternScanResult?.startIndex ?: throw PatchException("Could not find pattern to patch")
        val endIndex = patternScanResult.endIndex

        val register1 = (result.mutableMethod.getInstruction(index - 1) as BuilderInstruction35c).registerC
        val register2 = (result2.mutableMethod.getInstruction(startIndex - 2) as BuilderInstruction11x).registerA

        result.mutableMethod.addInstruction(
            endIndex - 3,
            "iput-object v$register2, v$register1, Lcom/ubnt/common/service/discovery/Version1Packet;->ipaddr:Ljava/lang/String;"
        )
    }
}