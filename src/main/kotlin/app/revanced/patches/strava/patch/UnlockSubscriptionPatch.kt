package app.revanced.patches.strava.patch

import app.revanced.patcher.annotation.Compatibility
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Package
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.replaceInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultError
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.strava.fingerprints.SubscriptionFingerprint

@Patch
@Name("Subscription features")
@Description("Enables Matched Runs and Segment Efforts.")
@Compatibility([Package("com.strava", ["320.12"])])
class UnlockSubscriptionPatch : BytecodePatch(
    listOf(SubscriptionFingerprint)
) {
    override fun execute(context: BytecodeContext): PatchResult {
        val result = SubscriptionFingerprint.result
            ?: return PatchResultError("Fingerprint not found")
        val patternScanResult = result.scanResult.patternScanResult
            ?: return PatchResultError("Fingerprint pattern not found")

        result.mutableMethod.replaceInstruction(patternScanResult.startIndex, "const/4 v0, 0x1")

        return PatchResultSuccess()
    }
}
