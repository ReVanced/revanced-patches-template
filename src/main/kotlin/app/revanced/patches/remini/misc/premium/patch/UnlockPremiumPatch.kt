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
import app.revanced.patches.remini.misc.premium.annotations.UnlockPremiumCompatibility
import app.revanced.patches.remini.misc.premium.fingerprints.UnlockPremiumFingerprint

@Patch
@Name("unlock-premium")
@Description("Unlocks premium-only functions.")
@UnlockPremiumCompatibility
@Version("0.0.1")
class UnlockPremiumPatch : BytecodePatch(
    listOf(UnlockPremiumFingerprint)
) {
    override fun execute(context: BytecodeContext): PatchResult {
        val result = UnlockPremiumFingerprint.result ?: return UnlockPremiumFingerprint.toErrorResult()
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