package app.revanced.patches.remini.misc.premium.patch

import app.revanced.extensions.toErrorResult
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.MethodFingerprintExtensions.name
import app.revanced.patcher.extensions.replaceInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultError
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.remini.misc.premium.annotations.PremiumUnlockCompatibility
import app.revanced.patches.remini.misc.premium.fingerprints.PremiumUnlockFingerprint

@Patch
@Name("unlock-premium")
@Description("Unlocks premium-only functions.")
@PremiumUnlockCompatibility
@Version("0.0.1")
class PremiumUnlockPatch : BytecodePatch(
    listOf(PremiumUnlockFingerprint)
) {
    override fun execute(context: BytecodeContext): PatchResult {
        val result = PremiumUnlockFingerprint.result ?: return PremiumUnlockFingerprint.toErrorResult()
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