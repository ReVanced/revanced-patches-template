package app.revanced.patches.youtube.layout.autoplaybutton.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.impl.BytecodeData
import app.revanced.patcher.extensions.addInstruction
import app.revanced.patcher.extensions.replaceInstructions
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.Dependencies
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.patch.impl.BytecodePatch
import app.revanced.patches.youtube.layout.autoplaybutton.annotations.AutoplayButtonCompatibility
import app.revanced.patches.youtube.layout.autoplaybutton.fingerprints.AutonavInformerFingerprint
import app.revanced.patches.youtube.layout.autoplaybutton.fingerprints.LayoutConstructorFingerprint
import app.revanced.patches.youtube.misc.mapping.patch.ResourceIdMappingProviderResourcePatch
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction
import org.jf.dexlib2.iface.instruction.WideLiteralInstruction

@Patch
@Dependencies([ResourceIdMappingProviderResourcePatch::class])
@Name("hide-autoplay-button")
@Description("Hides the autoplay button in the video player.")
@AutoplayButtonCompatibility
@Version("0.0.1")
class HideAutoplayButton : BytecodePatch(
    listOf(
        LayoutConstructorFingerprint, AutonavInformerFingerprint
    )
) {
    override fun execute(data: BytecodeData): PatchResult {
        val layoutGenMethod = LayoutConstructorFingerprint.result!!.mutableMethod

        val autonavToggle =
            ResourceIdMappingProviderResourcePatch.resourceMappings.single { it.type == "id" && it.name == "autonav_toggle" }
        val autonavPreviewStub =
            ResourceIdMappingProviderResourcePatch.resourceMappings.single { it.type == "id" && it.name == "autonav_preview_stub" }

        val instructions = layoutGenMethod.implementation!!.instructions

        val autonavToggleConstIndex =
            instructions.indexOfFirst { (it as? WideLiteralInstruction)?.wideLiteral == autonavToggle.id }
        val autonavPreviewStubConstIndex =
            instructions.indexOfFirst { (it as? WideLiteralInstruction)?.wideLiteral == autonavPreviewStub.id }

        val autonavToggleViewStubInsn = instructions.get(autonavToggleConstIndex + 2)
        val autonavPreviewStubInsn = instructions.get(autonavPreviewStubConstIndex + 2)

        //Register of the actual viewstub, should be v8
        val autonavToggleViewStubRegister = (autonavToggleViewStubInsn as? OneRegisterInstruction)?.registerA
        val autonavPreviewStubRegister = (autonavPreviewStubInsn as? OneRegisterInstruction)?.registerA

        layoutGenMethod.addInstruction(
            autonavToggleConstIndex + 4, """
           invoke-static {v$autonavToggleViewStubRegister}, Lapp/revanced/integrations/patches/HideAutoplayButtonPatch;->hideAutoplayButton(Landroid/view/ViewStub;)V  
        """
        )
        layoutGenMethod.addInstruction(
            autonavPreviewStubConstIndex + 4, """
            invoke-static {v$autonavPreviewStubRegister}, Lapp/revanced/integrations/patches/HideAutoplayButtonPatch;->hideAutoplayButton(Landroid/view/ViewStub;)V
        """
        )

        val autonavInformerMethod = AutonavInformerFingerprint.result!!.mutableMethod

        //force disable autoplay since it's hard to do without the button
        autonavInformerMethod.replaceInstructions(
            0, """
            invoke-static {}, Lapp/revanced/integrations/patches/HideAutoplayButtonPatch;->autoPlayEnabled()Z
            move-result v0            
            return v0
        """
        )

        return PatchResultSuccess()
    }
}
