package app.revanced.patches.youtube.layout.player.overlay.bytecode

import app.revanced.extensions.exception
import app.revanced.extensions.indexOfFirstConstantInstructionValue
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patches.youtube.layout.player.overlay.bytecode.fingerprints.CreatePlayerOverviewFingerprint
import app.revanced.patches.youtube.layout.player.overlay.resource.CustomPlayerOverlayOpacityResourcePatch
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction

@Patch(
    name = "Custom player overlay opacity",
    description = "Change the opacity of the player background, when player controls are visible.",
    dependencies = [ CustomPlayerOverlayOpacityResourcePatch::class ],
    compatiblePackages = [ CompatiblePackage("com.google.android.youtube") ]
)
object CustomPlayerOverlayOpacityPatch : BytecodePatch(
    setOf(CreatePlayerOverviewFingerprint)
) {
    override fun execute(context: BytecodeContext) {
        CreatePlayerOverviewFingerprint.result?.let { result ->
            result.mutableMethod.apply {
                val viewRegisterIndex =
                    indexOfFirstConstantInstructionValue(CustomPlayerOverlayOpacityResourcePatch.scrimOverlayId) + 3
                val viewRegister =
                    getInstruction<OneRegisterInstruction>(viewRegisterIndex).registerA

                val insertIndex = viewRegisterIndex + 1
                addInstruction(
                    insertIndex,
                    "invoke-static { v$viewRegister }, " +
                            "$INTEGRATIONS_CLASS_DESCRIPTOR->changeOpacity(Landroid/widget/ImageView;)V"
                )
            }
        } ?: throw CreatePlayerOverviewFingerprint.exception
    }

    const val INTEGRATIONS_CLASS_DESCRIPTOR = "Lapp/revanced/integrations/patches/CustomPlayerOverlayOpacityPatch;"
}