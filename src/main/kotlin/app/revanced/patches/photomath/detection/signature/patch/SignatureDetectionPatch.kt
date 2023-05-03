package app.revanced.patches.photomath.detection.signature.patch

import app.revanced.patcher.BytecodeContext
import app.revanced.extensions.error
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.extensions.instruction
import app.revanced.patcher.extensions.replaceInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchException
import app.revanced.patches.photomath.detection.signature.fingerprints.CheckSignatureFingerprint
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction

@Description("Disables detection of incorrect signature.")
@Version("0.0.2")
class SignatureDetectionPatch : BytecodePatch(
    listOf(
        CheckSignatureFingerprint
    )
) {
    override fun execute(context: BytecodeContext) {
        CheckSignatureFingerprint.result?.apply {
            val signatureCheckInstruction = mutableMethod.instruction(scanResult.patternScanResult!!.endIndex)
            val checkRegister = (signatureCheckInstruction as OneRegisterInstruction).registerA

            mutableMethod.replaceInstruction(signatureCheckInstruction.location.index, "const/4 v$checkRegister, 0x1")
        } ?: throw CheckSignatureFingerprint.error()

    }

}
