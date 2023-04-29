package app.revanced.patches.youtube.layout.hide.getpremium.bytecode.patch

import app.revanced.extensions.toErrorResult
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.extensions.instruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.youtube.layout.hide.getpremium.annotations.HideGetPremiumVideoAdvertisementCompatibility
import app.revanced.patches.youtube.layout.hide.getpremium.bytecode.fingerprints.GetPremiumVideoAdvertisementViewFingerprint
import app.revanced.patches.youtube.layout.hide.getpremium.resource.patch.HideGetPremiumVideoAdvertisementResourcePatch
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
import org.jf.dexlib2.iface.instruction.TwoRegisterInstruction

@Patch
@DependsOn([IntegrationsPatch::class, HideGetPremiumVideoAdvertisementResourcePatch::class])
@Name("hide-get-premium")
@Description("Hides advertisements for youtube premium in the video player.")
@HideGetPremiumVideoAdvertisementCompatibility
@Version("0.0.1")
class HideGetPremiumVideoAdvertisementPatch : BytecodePatch(
    listOf(
        GetPremiumVideoAdvertisementViewFingerprint,
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {
        GetPremiumVideoAdvertisementViewFingerprint.result?.let {
            it.mutableMethod.apply {
                val startIndex = it.scanResult.patternScanResult!!.startIndex
                val measuredWidthRegister = (instruction(startIndex) as TwoRegisterInstruction).registerA
                val measuredHeightInstruction = instruction(startIndex + 1) as TwoRegisterInstruction
                val measuredHeightRegister = measuredHeightInstruction.registerA
                val tempRegister = measuredHeightInstruction.registerB

                addInstructions(
                    startIndex + 2,
                    """
                        invoke-static {}, $INTEGRATIONS_CLASS_DESCRIPTOR->hideGetPremiumView()Z
                        move-result v$tempRegister
                        if-eqz v$tempRegister, :allow
                        const/4 v$measuredWidthRegister, 0x0
                        const/4 v$measuredHeightRegister, 0x0
                        :allow
                        nop
                    """
                )
            }
        } ?: return GetPremiumVideoAdvertisementViewFingerprint.toErrorResult()

        return PatchResultSuccess()
    }

    private companion object {
        const val INTEGRATIONS_CLASS_DESCRIPTOR =
            "Lapp/revanced/integrations/patches/HideGetPremiumPatch;"
    }
}
