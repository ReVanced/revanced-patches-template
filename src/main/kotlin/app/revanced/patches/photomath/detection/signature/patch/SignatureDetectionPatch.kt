package app.revanced.patches.photomath.detection.signature.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.instruction
import app.revanced.patcher.extensions.replaceInstruction
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint.Companion.resolve
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patches.photomath.detection.signature.annotations.DisableSignatureDetectionCompatibility
import app.revanced.patches.photomath.detection.signature.fingerprints.CheckSignatureFingerprint
import app.revanced.patches.photomath.detection.signature.fingerprints.MainActivityOnCreateFingerprint
import app.revanced.patches.photomath.detection.signature.fingerprints.MainOnCreateFingerprint
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction

@Description("Disables detection of incorrect signature.")
@DisableSignatureDetectionCompatibility
@Version("0.0.2")
class SignatureDetectionPatch : BytecodePatch(
    listOf(
        MainActivityOnCreateFingerprint
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {
        // As different versions of the app have signature verification in different methods,
        // we may need to update the fingerprint result.
        // The actual verification process is the same, just the method is different.
        var onCreate = MainActivityOnCreateFingerprint.result!!

        val patternResult = CheckSignatureFingerprint.also { fingerprint ->
            // Try resolving the new fingerprint (from 8.21.0 onwards)
            if (!fingerprint.resolve(context, onCreate.method, onCreate.classDef)) {
                // If the fingerprint is not resolved, try resolving the old fingerprint (up to 8.20.0)
                onCreate = MainOnCreateFingerprint.also {
                    listOf(it).resolve(context, context.classes)
                }.result!!
                fingerprint.resolve(context, onCreate.method, onCreate.classDef)
            }
        }.result!!.scanResult.patternScanResult!!

        onCreate.mutableMethod.apply {
            val signatureCheckInstruction = instruction(patternResult.endIndex)
            val checkRegister = (signatureCheckInstruction as OneRegisterInstruction).registerA

            replaceInstruction(signatureCheckInstruction.location.index, "const/4 v$checkRegister, 0x1")
        }

        return PatchResultSuccess()
    }

}