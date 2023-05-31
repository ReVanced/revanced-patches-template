package app.revanced.patches.youtube.layout.hide.crowdfundingbox.bytecode.patch

import app.revanced.extensions.error
import app.revanced.patcher.BytecodeContext
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.extensions.addInstruction
import app.revanced.patcher.extensions.instruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.youtube.layout.hide.crowdfundingbox.annotations.CrowdfundingBoxCompatibility
import app.revanced.patches.youtube.layout.hide.crowdfundingbox.bytecode.fingerprints.CrowdfundingBoxFingerprint
import app.revanced.patches.youtube.layout.hide.crowdfundingbox.resource.patch.CrowdfundingBoxResourcePatch
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
import org.jf.dexlib2.iface.instruction.TwoRegisterInstruction

@Patch
@DependsOn([IntegrationsPatch::class, CrowdfundingBoxResourcePatch::class])
@Name("hide-crowdfunding-box")
@Description("Hides the crowdfunding box between the player and video description.")
@CrowdfundingBoxCompatibility
@Version("0.0.1")
class CrowdfundingBoxPatch : BytecodePatch(
    listOf(
        CrowdfundingBoxFingerprint,
    )
) {
    override suspend fun execute(context: BytecodeContext) {
        CrowdfundingBoxFingerprint.result?.let {
            it.mutableMethod.apply {
                val insertIndex = it.scanResult.patternScanResult!!.endIndex
                val objectRegister = instruction<TwoRegisterInstruction>(insertIndex).registerA

                addInstruction(insertIndex, "invoke-static {v$objectRegister}, $INTEGRATIONS_METHOD_DESCRIPTOR")
            }
        } ?: CrowdfundingBoxFingerprint.error()

    }

    private companion object {
        const val INTEGRATIONS_METHOD_DESCRIPTOR =
            "Lapp/revanced/integrations/patches/HideCrowdfundingBoxPatch;->hideCrowdfundingBox(Landroid/view/View;)V"
    }
}
