package app.revanced.patches.grindr.unlimited.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.extensions.exception


import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patches.grindr.unlimited.annotations.UnlockUnlimitedCompatibility
import app.revanced.patches.grindr.annotations.GrindrPatchCompatibility

import app.revanced.patches.grindr.unlimited.fingerprints.*
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import app.revanced.patcher.extensions.InstructionExtensions.replaceInstructions
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint

import app.revanced.patches.grindr.patch.FirebaseGetCertPatch

@Patch
@Name("Unlock unlimited")
@Description("Unlocks unlimited features.")
@UnlockUnlimitedCompatibility
@DependsOn([FirebaseGetCertPatch::class])
@GrindrPatchCompatibility

class UnlockUnlimitedPatch : BytecodePatch(
    listOf(
        HasFeatureFingerprint,
        IsFreeFingerprint,
        IsNoPlusUpsellFingerprint,
        IsNoXtraUpsellFingerprint,
        IsPlusFingerprint,
        IsUnlimitedFingerprint,
        IsXtraFingerprint,
        InnaccessibleProfileManagerbFingerprint,
        InnaccessibleProfileManagerdFingerprint
    )
) {

    class Feature(val feature: MethodFingerprint, val bytecode: String, val offset: Int = 0) {

        fun execute(context: BytecodeContext) {
            feature.result?.let { result ->
                result.mutableMethod.apply {
                    replaceInstructions(offset, bytecode)
                }
            } ?: throw feature.exception
        }

    }


    override fun execute(context: BytecodeContext) {

        val TRUE_BYTECODE = """
            const/4 v0, 0x1
            return v0
        """.trimIndent()

        val FALSE_BYTECODE = """
            const/4 v0, 0x0
            return v0
        """.trimIndent()

        /*
            Based on: https://github.com/ElJaviLuki/GrindrPlus
        */

        val features = arrayOf(
            Feature(HasFeatureFingerprint, TRUE_BYTECODE),
            Feature(IsFreeFingerprint, FALSE_BYTECODE),
            Feature(IsNoPlusUpsellFingerprint, TRUE_BYTECODE),
            Feature(IsNoXtraUpsellFingerprint, TRUE_BYTECODE),
            Feature(IsPlusFingerprint, FALSE_BYTECODE),
            Feature(IsUnlimitedFingerprint, TRUE_BYTECODE),
            Feature(IsXtraFingerprint, FALSE_BYTECODE),
            Feature(InnaccessibleProfileManagerbFingerprint, TRUE_BYTECODE),
            Feature(InnaccessibleProfileManagerdFingerprint, FALSE_BYTECODE)
        )

        for (feature in features) {
            feature.execute(context)
        }
    }
    
}