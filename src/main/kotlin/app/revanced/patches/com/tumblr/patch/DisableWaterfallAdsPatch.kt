package app.revanced.patches.com.tumblr.patch

import app.revanced.extensions.exception
import app.revanced.patcher.annotation.Compatibility
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Package
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.extensions.InstructionExtensions.replaceInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.com.tumblr.fingerprints.AdWaterfallFingerprint
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction

// Tested on 31.1.0.110, but this patch should be resilient enough to last a loong time
@Patch
@Name("Disable ads")
@Description("Disables ads in the dashboard")
@Compatibility([Package("com.tumblr")])
class DisableWaterfallAdsPatch : BytecodePatch(
    listOf(AdWaterfallFingerprint)
) {
    override fun execute(context: BytecodeContext) {
        val result = AdWaterfallFingerprint.result
            ?: throw AdWaterfallFingerprint.exception

        val matches = result.scanResult.stringsScanResult!!.matches

        // We just replace all occourances of "client_side_ad_waterfall" with anything else
        // so the app fails to handle Ads in the timeline elements array and just skips them
        // See AdWaterfallFingerprint for more info
        for (match in matches) {
            val instr = result.mutableMethod.getInstruction<OneRegisterInstruction>(match.index)
            val register = instr.registerA
            result.mutableMethod.replaceInstruction(
                match.index, """
                const-string v$register, "nope"
                """
            )
        }
    }
}