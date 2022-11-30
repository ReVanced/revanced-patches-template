package app.revanced.patches.remini.misc.premium.patch

import app.revanced.extensions.toErrorResult
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.replaceInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.Patch

@Patch
@Name("unlock-premium")
@Description("Unlocks premium-only functions.")
@UnlockPremiumPatch
@Version("0.0.1")
class UnlockPremiumPatch : BytecodePatch(
    listOf(UnlockPremiumPatch)
) {
    override fun execute(context: BytecodeContext): PatchResult {
        val result = UnlockPremiumPatch.result ?: return UnlockPremiumPatch.toErrorResult()
        val index = result.scanResult.patternScanResult!!.endIndex

        result.mutableMethod.replaceInstruction(
            index,
            """
                const/4 v1, 0x1
            """
        )

        return PatchResultSuccess()
    }
}