package app.revanced.patches.youtube.layout.hide.player.overlay.bytecode.patch

import app.revanced.extensions.toErrorResult
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.addInstruction
import app.revanced.patcher.extensions.instruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.youtube.layout.hide.player.overlay.annotations.HidePlayerOverlayPatchCompatibility
import app.revanced.patches.youtube.layout.hide.player.overlay.bytecode.fingerprints.HidePlayerOverlayFingerprint
import app.revanced.patches.youtube.layout.hide.player.overlay.resource.patch.HidePlayerOverlayResourcePatch
import org.jf.dexlib2.iface.instruction.WideLiteralInstruction
import org.jf.dexlib2.iface.instruction.formats.Instruction21c

@Patch
@Name("hide-player-overlay")
@Description("Hides the dark player overlay when player controls are visible.")
@DependsOn([HidePlayerOverlayResourcePatch::class])
@HidePlayerOverlayPatchCompatibility
@Version("0.0.2")
class HidePlayerOverlayPatch : BytecodePatch(
    listOf(
        HidePlayerOverlayFingerprint
    )
) {
    private companion object {
        const val INTEGRATIONS_CLASS_DESCRIPTOR = "Lapp/revanced/integrations/patches/HidePlayerOverlayPatch;"
    }

    override fun execute(context: BytecodeContext): PatchResult {
        HidePlayerOverlayFingerprint.result?.let { result ->
            result.mutableMethod.apply {
                val instructions = implementation!!.instructions
                val viewRegisterIndex = instructions.indexOfFirst {
                    (it as? WideLiteralInstruction)?.wideLiteral == HidePlayerOverlayResourcePatch.scrimOverlayId
                } + 3
                val viewRegister = (instruction(viewRegisterIndex) as Instruction21c).registerA

                addInstruction(
                    viewRegisterIndex + 1,
                    "invoke-static {v$viewRegister}, $INTEGRATIONS_CLASS_DESCRIPTOR->hidePlayerOverlay(Landroid/widget/ImageView;)V"
                )
            }
        } ?: return HidePlayerOverlayFingerprint.toErrorResult()

        return PatchResultSuccess()
    }
}
