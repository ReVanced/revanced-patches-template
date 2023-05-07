package app.revanced.patches.messenger.compose.patch

import app.revanced.extensions.toErrorResult
import app.revanced.patcher.annotation.Compatibility
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Package
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.instruction
import app.revanced.patcher.extensions.replaceInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.messenger.compose.fingerprints.SwitchComposeButtonFingerprint
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction

@Patch
@Name("disable-compose-tab-switch")
@Description("Disables switching to sticker search mode in compose field")
@Compatibility([Package("com.facebook.orca")])
@Version("0.0.1")
class DisableComposeExpressionTabChange : BytecodePatch(
    listOf(SwitchComposeButtonFingerprint)
) {
    override fun execute(context: BytecodeContext): PatchResult {
        SwitchComposeButtonFingerprint.result?.apply {
            val setStringInstruction = mutableMethod.instruction(scanResult.patternScanResult!!.startIndex + 2)
            val targetRegister = (setStringInstruction as OneRegisterInstruction).registerA

            mutableMethod.replaceInstruction(setStringInstruction.location.index, "const-string v$targetRegister, \"expression\"")
        } ?: throw SwitchComposeButtonFingerprint.toErrorResult()
        return PatchResultSuccess()
    }
}
