package app.revanced.patches.photomath.detection.signature.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint.Companion.resolve
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultError
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patches.photomath.detection.signature.annotations.DisableSignatureDetectionCompatibilty
import app.revanced.patches.photomath.detection.signature.fingerprints.MainOnCreateFingerprint
import org.jf.dexlib2.Opcode
import org.jf.dexlib2.builder.instruction.BuilderInstruction11n
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction

@Description("Disables detection of incorrect signature.")
@DisableSignatureDetectionCompatibilty
@Version("0.0.1")
class SignatureDetectionPatch : BytecodePatch(
    listOf(
        MainOnCreateFingerprint
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {
        val onCreateMethod = MainOnCreateFingerprint.result!!
        val checkSignatureFingerprint = object : MethodFingerprint(
            strings = listOf(
                "currentSignature"
            ),
            opcodes = listOf(
                Opcode.CONST_STRING,
                Opcode.CONST_STRING,
                Opcode.INVOKE_STATIC,
                Opcode.INVOKE_STATIC,
                Opcode.MOVE_RESULT_OBJECT,
                Opcode.INVOKE_VIRTUAL,
                Opcode.MOVE_RESULT_OBJECT,
                Opcode.INVOKE_STATIC,
            )
        ) {}

        val patternResult = checkSignatureFingerprint.also {
            it.resolve(
                context,
                onCreateMethod.method,
                onCreateMethod.classDef,
            )
        }.result!!.scanResult.patternScanResult!!

        with(onCreateMethod.mutableMethod.implementation!!) {
            val signCheckResultInstr = this.instructions[patternResult.endIndex + 1]!!
            if (signCheckResultInstr.opcode!=Opcode.MOVE_RESULT) return PatchResultError("Can't find result of signature check")
            this.replaceInstruction(
                signCheckResultInstr.location.index,
                BuilderInstruction11n(
                    Opcode.CONST_4,
                    (signCheckResultInstr as OneRegisterInstruction).registerA,
                    1
                )
            )
        }

        return PatchResultSuccess()
    }

}