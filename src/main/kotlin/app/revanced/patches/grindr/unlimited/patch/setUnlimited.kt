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
import app.revanced.patches.grindr.unlimited.fingerprints.IsUnlimitedFingerprint
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
    listOf(IsUnlimitedFingerprint)
) {
    override fun execute(context: BytecodeContext): PatchResult {
        println("Unlocking unlimited features...")
        IsUnlimitedFingerprint.result?.let { result ->
            println("Found fingerprint!")

            result.mutableMethod.apply {
                replaceInstructions(
                    0,
                    """
                        const/4 v0, 0x1
                        return v0                    
                    """
                )
            }
            

        } ?: return IsUnlimitedFingerprint.toErrorResult()

        return PatchResultSuccess()
    }
}