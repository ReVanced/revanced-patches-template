package app.revanced.patches.strava.subscription.patch

import app.revanced.patcher.annotation.Compatibility
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Package
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.replaceInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchException
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.strava.subscription.fingerprints.GetSubscribedFingerprint

@Patch
@Name("Unlock subscription features")
@Description("Unlocks \"Matched Runs\" and \"Segment Efforts\".")
@Compatibility([Package("com.strava", ["320.12"])])
class UnlockSubscriptionPatch : BytecodePatch(
    listOf(GetSubscribedFingerprint)
) {
    override fun execute(context: BytecodeContext) {
        val result = GetSubscribedFingerprint.result
            ?: throw PatchException("Fingerprint not found")
        val patternScanResult = result.scanResult.patternScanResult
            ?: throw PatchException("Fingerprint pattern not found")

        result.mutableMethod.replaceInstruction(patternScanResult.startIndex, "const/4 v0, 0x1")
    }
}
