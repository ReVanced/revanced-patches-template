package app.revanced.patches.tumblr.live.patch

import app.revanced.extensions.exception
import app.revanced.patcher.annotation.Compatibility
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Package
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.extensions.InstructionExtensions.replaceInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.tumblr.featureflags.patch.OverrideFeatureFlagsPatch
import app.revanced.patches.tumblr.live.fingerprints.LiveMarqueeFingerprint
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction

@Patch
@Name("Disable Tumblr Live")
@Description("Disable the Tumblr Live tab button and dashboard carousel.")
@DependsOn([OverrideFeatureFlagsPatch::class])
@Compatibility([Package("com.tumblr")])
class DisableTumblrLivePatch : BytecodePatch(
    listOf(LiveMarqueeFingerprint)
) {
    override fun execute(context: BytecodeContext) = LiveMarqueeFingerprint.result?.let {
        it.scanResult.stringsScanResult!!.matches.forEach { match ->
            // Replace the string constant "live_marquee"
            // with a dummy so the app doesn't recognize this type of element in the Dashboard and skips it
            it.mutableMethod.apply {
                val stringRegister = getInstruction<OneRegisterInstruction>(match.index).registerA
                replaceInstruction(match.index, "const-string v$stringRegister, \"dummy2\"")
            }

        }

        // We hide the Tab button for Tumblr Live by forcing the feature flag to false
        OverrideFeatureFlagsPatch.addOverride("liveStreaming", "false")
    } ?: throw LiveMarqueeFingerprint.exception
}