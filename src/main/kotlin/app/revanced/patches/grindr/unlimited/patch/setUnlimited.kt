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
import app.revanced.patches.grindr.unlimited.fingerprints.*
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import app.revanced.patcher.extensions.InstructionExtensions.replaceInstructions
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
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
    override fun execute(context: BytecodeContext) {

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
        } ?: throw HasFeatureFingerprint.exception

        IsFreeFingerprint.result?.let { result ->
            println("Found IsFreeFingerprint!")
            result.mutableMethod.apply {
                addInstruction(3, """
                    xor-int/lit8 v0, v0, 0x1
                """.trimIndent())
            }
        } ?: throw IsFreeFingerprint.exception

        IsNoPlusUpsellFingerprint.result?.let { result ->
            println("Found IsNoPlusUpsellFingerprint!")
            result.mutableMethod.apply {
                replaceInstructions(0, _true)
            }
        } ?: throw IsNoPlusUpsellFingerprint.exception

        IsNoXtraUpsellFingerprint.result?.let { result ->
            println("Found IsNoXtraUpsellFingerprint!")
            result.mutableMethod.apply {
                replaceInstructions(0, _true)
            }
        } ?: throw IsNoXtraUpsellFingerprint.exception

        IsPlusFingerprint.result?.let { result ->
            println("Found IsPlusFingerprint!")
            result.mutableMethod.apply {
                replaceInstructions(0, _true)
            }
        } ?: throw IsPlusFingerprint.exception

        IsUnlimitedFingerprint.result?.let { result ->
            println("Found IsUnlimitedFingerprint!")
            result.mutableMethod.apply {
                replaceInstructions(0, _true)
            }
        } ?: throw IsUnlimitedFingerprint.exception

        IsXtraFingerprint.result?.let { result ->
            println("Found IsXtraFingerprint!")
            result.mutableMethod.apply {
                replaceInstructions(0, _true)
            }
        } ?: throw IsXtraFingerprint.exception

        //this must always be true
        InnaccessibleProfileManagerbFingerprint.result?.let { result ->
            println("Found InnaccessibleProfileManagerbFingerprint!")
            result.mutableMethod.apply {
                replaceInstructions(0, _true)
            }
        } ?: throw InnaccessibleProfileManagerbFingerprint.exception

        //this must always be false (the opposite of InnaccessibleProfileManagerbFingerprint)
        InnaccessibleProfileManagerdFingerprint.result?.let { result ->
            println("Found InnaccessibleProfileManagerdFingerprint!")
            result.mutableMethod.apply {
                replaceInstructions(2, _false)
            }
        } ?: throw InnaccessibleProfileManagerdFingerprint.exception
    }
}