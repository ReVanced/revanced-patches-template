package app.revanced.patches.grindr.unlimited.patch

import app.revanced.extensions.toErrorResult
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patches.grindr.unlimited.annotations.UnlockUnlimitedCompatibility
import app.revanced.patches.grindr.unlimited.fingerprints.*
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction
import app.revanced.patcher.extensions.InstructionExtensions.replaceInstructions
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction

import app.revanced.patches.grindr.patch.bytecode.FirebaseGetCertPatch

@Patch
@Name("Unlock unlimited")
@Description("Unlocks unlimited features.")
@UnlockUnlimitedCompatibility
@DependsOn([FirebaseGetCertPatch::class])
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
    override fun execute(context: BytecodeContext): PatchResult {

        val _true = """
            const/4 v0, 0x1
            return v0
        """.trimIndent()

        val _false = """
            const/4 v0, 0x0
            return v0
        """.trimIndent()

        /*
            Based on: https://github.com/ElJaviLuki/GrindrPlus
         */

        HasFeatureFingerprint.result?.let { result ->
            println("Found HasFeatureFingerprint!")
            result.mutableMethod.apply {
                replaceInstructions(0, _true)
            }
        } ?: return HasFeatureFingerprint.toErrorResult()

        IsFreeFingerprint.result?.let { result ->
            println("Found IsFreeFingerprint!")
            result.mutableMethod.apply {
                replaceInstructions(0, _false)
            }
        } ?: return IsFreeFingerprint.toErrorResult()

        IsNoPlusUpsellFingerprint.result?.let { result ->
            println("Found IsNoPlusUpsellFingerprint!")
            result.mutableMethod.apply {
                replaceInstructions(0, _true)
            }
        } ?: return IsNoPlusUpsellFingerprint.toErrorResult()

        IsNoXtraUpsellFingerprint.result?.let { result ->
            println("Found IsNoXtraUpsellFingerprint!")
            result.mutableMethod.apply {
                replaceInstructions(0, _true)
            }
        } ?: return IsNoXtraUpsellFingerprint.toErrorResult()

        IsPlusFingerprint.result?.let { result ->
            println("Found IsPlusFingerprint!")
            result.mutableMethod.apply {
                replaceInstructions(0, _true)
            }
        } ?: return IsPlusFingerprint.toErrorResult()

        IsUnlimitedFingerprint.result?.let { result ->
            println("Found IsUnlimitedFingerprint!")
            result.mutableMethod.apply {
                replaceInstructions(0, _true)
            }
        } ?: return IsUnlimitedFingerprint.toErrorResult()

        IsXtraFingerprint.result?.let { result ->
            println("Found IsXtraFingerprint!")
            result.mutableMethod.apply {
                replaceInstructions(0, _true)
            }
        } ?: return IsXtraFingerprint.toErrorResult()

        //this must always be true
        InnaccessibleProfileManagerbFingerprint.result?.let { result ->
            println("Found InnaccessibleProfileManagerbFingerprint!")
            result.mutableMethod.apply {
                replaceInstructions(0, _true)
            }
        } ?: return InnaccessibleProfileManagerbFingerprint.toErrorResult()

        //this must always be false (the opposite of InnaccessibleProfileManagerbFingerprint)
        InnaccessibleProfileManagerdFingerprint.result?.let { result ->
            println("Found InnaccessibleProfileManagerdFingerprint!")
            result.mutableMethod.apply {
                replaceInstructions(0, _false)
            }
        } ?: return InnaccessibleProfileManagerdFingerprint.toErrorResult()

        
        return PatchResultSuccess()
    }
}