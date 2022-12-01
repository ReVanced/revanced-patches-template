package app.revanced.patches.urbandroid.sleep.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.urbandroid.sleep.annotations.UnlockPremiumCompatibility
import app.revanced.patches.urbandroid.sleep.fingerprints.IsTrialFingerprint

@Patch
@Name("sleep-premium-unlock")
@Description("Unlocks all premium features.")
@UnlockPremiumCompatibility
class UnlockPremiumPatch : BytecodePatch(
    listOf(
        IsTrialFingerprint
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {
        val method = IsTrialFingerprint.result!!.mutableMethod

        method.addInstructions(
            0,
            """
                const/4 v0, 0x0
                return v0
            """
        )

        return PatchResultSuccess()
    }
}