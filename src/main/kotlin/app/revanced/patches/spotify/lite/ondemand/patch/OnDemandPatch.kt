package app.revanced.patches.spotify.lite.ondemand.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.addInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.spotify.lite.ondemand.annotations.OnDemandCompatibility
import app.revanced.patches.spotify.lite.ondemand.fingerprints.OnDemandFingerprint

@Patch
@Name("enable-ondemand")
@Description("Enables On-Demand to play any song from any artist.")
@OnDemandCompatibility
@Version("0.0.1")
class OnDemandPatch : BytecodePatch(
    listOf(
        OnDemandFingerprint
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {
        OnDemandFingerprint.result?.apply {
            val insertIndex = scanResult.patternScanResult!!.endIndex - 1
            // Force the UI to behave like with a Premium account
            mutableMethod.addInstruction(insertIndex,"const/4 v0, 0x2")
        }
        return PatchResultSuccess()
    }
}
