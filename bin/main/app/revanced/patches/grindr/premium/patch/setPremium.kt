package app.revanced.patches.grindr.premium.patch

import app.revanced.extensions.toErrorResult
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.grindr.premium.annotations.UnlockPremiumCompatibility
import app.revanced.patches.grindr.premium.fingerprints.IsPremiumFingerprint
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction

@Patch
@Name("Unlock premium")
@Description("Unlocks premium features.")
@UnlockPremiumCompatibility
class UnlockPremiumPatch : BytecodePatch(
    listOf(IsPremiumFingerprint)
) {
    override fun execute(context: BytecodeContext): PatchResult {
        println("Unlocking premium features...")
        IsPremiumFingerprint.result?.let { result ->
            println("Found fingerprint!")

            result.mutableMethod.apply {
                addInstruction(
                    0,
                    """
                        const/4 v0, 0x1
                        return v0                    
                    """
                )
            }
            

        } ?: return IsPremiumFingerprint.toErrorResult()

        return PatchResultSuccess()
    }
}