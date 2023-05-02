package app.revanced.patches.youtube.layout.hide.player.overlay.patch

import app.revanced.extensions.findMutableMethodOf
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.addInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.shared.mapping.misc.patch.ResourceMappingPatch
import app.revanced.patches.shared.settings.preference.impl.StringResource
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.youtube.layout.hide.player.overlay.annotations.HidePlayerOverlayPatchCompatibility
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch
import org.jf.dexlib2.Opcode
import org.jf.dexlib2.iface.instruction.formats.Instruction21c
import org.jf.dexlib2.iface.instruction.formats.Instruction31i

@Patch
@Name("hide-player-overlay")
@Description("Hides the dark player overlay when player controls are visible.")
@HidePlayerOverlayPatchCompatibility
@Version("0.0.2")
class HidePlayerOverlayPatch : BytecodePatch() {
    private companion object {
        val scrimOverlayId = ResourceMappingPatch.resourceMappings.single {
            it.name == "scrim_overlay"
        }.id
        const val INTEGRATIONS_CLASS_DESCRIPTOR = "Lapp/revanced/integrations/patches/HidePlayerOverlayPatch;"
    }

    override fun execute(context: BytecodeContext): PatchResult {
        SettingsPatch.PreferenceScreen.LAYOUT.addPreferences(
            SwitchPreference(
                "revanced_hide_player_overlay",
                StringResource("revanced_hide_player_overlay_title", "Hide player overlay"),
                false,
                StringResource("revanced_hide_player_overlay_summary_on", "Player overlay is hidden"),
                StringResource("revanced_hide_player_overlay_summary_off", "Player overlay is shown")
            )
        )

        context.classes.forEach { classDef ->
            classDef.methods.forEach { method ->
                with(method.implementation) {
                    this?.instructions?.forEachIndexed { index, instruction ->
                        when (instruction.opcode) {
                            Opcode.CONST -> {
                                when ((instruction as Instruction31i).wideLiteral) {
                                    scrimOverlayId -> { // player overlay filter
                                        val insertIndex = index + 3
                                        val invokeInstruction = instructions.elementAt(insertIndex)
                                        if (invokeInstruction.opcode != Opcode.CHECK_CAST) return@forEachIndexed

                                        val mutableMethod = context.proxy(classDef).mutableClass.findMutableMethodOf(method)
                                        val viewRegister = (invokeInstruction as Instruction21c).registerA

                                        mutableMethod.addInstruction(
                                            insertIndex + 1,
                                            "invoke-static {v$viewRegister}, $INTEGRATIONS_CLASS_DESCRIPTOR->hidePlayerOverlay(Landroid/widget/ImageView;)V"
                                        )
                                    }
                                }
                            }
                            else -> return@forEachIndexed
                        }
                    }
                }
            }
        }

        return PatchResultSuccess()
    }
}
