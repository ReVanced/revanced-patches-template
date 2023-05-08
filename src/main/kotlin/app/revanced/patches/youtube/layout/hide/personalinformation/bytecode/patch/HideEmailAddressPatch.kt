package app.revanced.patches.youtube.layout.hide.personalinformation.bytecode.patch

import app.revanced.patcher.BytecodeContext
import app.revanced.extensions.error
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.extensions.instruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.youtube.layout.hide.personalinformation.annotations.HideEmailAddressCompatibility
import app.revanced.patches.youtube.layout.hide.personalinformation.bytecode.fingerprints.AccountSwitcherAccessibilityLabelFingerprint
import app.revanced.patches.youtube.layout.hide.personalinformation.resource.patch.HideEmailAddressResourcePatch
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction

@Patch
@DependsOn([IntegrationsPatch::class, HideEmailAddressResourcePatch::class])
@Name("hide-email-address")
@Description("Hides the email address in the account switcher.")
@HideEmailAddressCompatibility
@Version("0.0.1")
class HideEmailAddressPatch : BytecodePatch(
    listOf(
        AccountSwitcherAccessibilityLabelFingerprint
    )
) {
    override fun execute(context: BytecodeContext) {
        AccountSwitcherAccessibilityLabelFingerprint.result?.let {
            it.mutableMethod.apply {
                val setVisibilityConstIndex = it.scanResult.patternScanResult!!.endIndex

                val setVisibilityConstRegister =
                    instruction<OneRegisterInstruction>(setVisibilityConstIndex - 2).registerA

                addInstructions(
                    setVisibilityConstIndex,
                    """
                        invoke-static {v$setVisibilityConstRegister}, Lapp/revanced/integrations/patches/HideEmailAddressPatch;->hideEmailAddress(I)I
                        move-result v$setVisibilityConstRegister
                    """
                )
            }
        } ?: AccountSwitcherAccessibilityLabelFingerprint.error()

    }
}
