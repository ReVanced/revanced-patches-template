package app.revanced.patches.lightroom.misc.pro.patch

import app.revanced.extensions.toErrorResult
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.replaceInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.lightroom.misc.pro.annotations.UnlockPremiumCompatibility
import app.revanced.patches.lightroom.misc.pro.fingerprint.UnlockPremiumFingerprint

@Patch
@Name("Unlock premium")
@Description("Unlocks premium features.")
@UnlockPremiumCompatibility
class UnlockPremiumVersionPatch : BytecodePatch(
    listOf(
        UnlockPremiumFingerprint
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {
        UnlockPremiumFingerprint.result?.apply {
            mutableMethod.replaceInstruction(2,
                """
                    const/4 v2, 0x1
                """
            )
        } ?: throw UnlockPremiumFingerprint.toErrorResult()

        return PatchResultSuccess()
    }
}