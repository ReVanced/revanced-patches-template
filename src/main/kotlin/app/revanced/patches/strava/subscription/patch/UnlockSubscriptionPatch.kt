package app.revanced.patches.strava.subscription.patch

import app.revanced.extensions.exception
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.replaceInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.strava.annotations.StravaCompatibility
import app.revanced.patches.strava.subscription.fingerprints.GetSubscribedFingerprint

@Patch
@Name("Unlock subscription features")
@Description("Unlocks Routes, Matched Runs and Segment Efforts.")
@StravaCompatibility
class UnlockSubscriptionPatch : BytecodePatch(listOf(GetSubscribedFingerprint)) {
    override fun execute(context: BytecodeContext) = GetSubscribedFingerprint.result?.let { result ->
        val isSubscribedIndex = result.scanResult.patternScanResult!!.startIndex
        result.mutableMethod.replaceInstruction(isSubscribedIndex, "const/4 v0, 0x1")
    } ?: throw GetSubscribedFingerprint.exception
}
