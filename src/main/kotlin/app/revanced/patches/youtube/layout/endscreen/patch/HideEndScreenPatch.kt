package app.revanced.patches.youtube.layout.endscreen.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.addInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.youtube.ad.general.annotations.GeneralAdsRemovalPatchCompatibility
import app.revanced.patches.youtube.layout.endscreen.fingerprints.EndScreenElementLayoutCircleFingerprint
import app.revanced.patches.youtube.layout.endscreen.fingerprints.EndScreenElementLayoutIconFingerprint
import app.revanced.patches.youtube.layout.endscreen.fingerprints.EndScreenElementLayoutVideoFingerprint
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
import app.revanced.patches.youtube.misc.mapping.patch.ResourceMappingResourcePatch
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch
import app.revanced.patches.youtube.misc.settings.framework.components.impl.StringResource
import app.revanced.patches.youtube.misc.settings.framework.components.impl.SwitchPreference
import org.jf.dexlib2.builder.BuilderInstruction
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction
import org.jf.dexlib2.iface.instruction.WideLiteralInstruction

@Patch
@DependsOn([IntegrationsPatch::class, SettingsPatch::class, ResourceMappingResourcePatch::class])
@Name("hide-end-screen")
@Description("Hides the end screen at the end of videos.")
@GeneralAdsRemovalPatchCompatibility
@Version("0.0.1")
class HideEndScreenPatch : BytecodePatch(
    listOf(
        EndScreenElementLayoutCircleFingerprint,
        EndScreenElementLayoutIconFingerprint,
        EndScreenElementLayoutVideoFingerprint,
    )
) {
    internal companion object {
        // list of resource names to get the id of
        var resourceIds = arrayOf(
            "endscreen_element_layout_circle",
            "endscreen_element_layout_icon",
            "endscreen_element_layout_video",
        ).map { name ->
            ResourceMappingResourcePatch.resourceMappings.single { it.name == name }.id
        }
    }

    override fun execute(data: BytecodeContext): PatchResult {
        SettingsPatch.PreferenceScreen.LAYOUT.addPreferences(
            SwitchPreference(
                "revanced_endscreen_enabled",
                StringResource("revanced_endscreen_enabled_title", "Hide the end screen"),
                true,
                StringResource("revanced_endscreen_enabled_summary_on", "End screen is hidden"),
                StringResource("revanced_endscreen_enabled_summary_off", "End screen is shown")
            ),
        )

        val firstJumpIndex = 4
        val secondJumpIndex = 1

        val endScreenElementLayoutCircleMethod = EndScreenElementLayoutCircleFingerprint.result!!.mutableMethod
        val endScreenElementLayoutCircleInstructions = endScreenElementLayoutCircleMethod.implementation!!.instructions
        val circleCheckCastIndex = endScreenElementLayoutCircleInstructions.indexOfFirst {
            (it as? WideLiteralInstruction)?.wideLiteral == resourceIds[0]
        } + firstJumpIndex

        val endScreenElementLayoutIconMethod = EndScreenElementLayoutIconFingerprint.result!!.mutableMethod
        val endScreenElementLayoutIconInstructions = endScreenElementLayoutIconMethod.implementation!!.instructions
        val iconCheckCastIndex = endScreenElementLayoutCircleInstructions.indexOfFirst {
            (it as? WideLiteralInstruction)?.wideLiteral == resourceIds[0]
        } + firstJumpIndex

        val endScreenElementLayoutVideoMethod = EndScreenElementLayoutVideoFingerprint.result!!.mutableMethod
        val endScreenElementLayoutVideoInstructions = endScreenElementLayoutVideoMethod.implementation!!.instructions
        val videoCheckCastIndex = endScreenElementLayoutCircleInstructions.indexOfFirst {
            (it as? WideLiteralInstruction)?.wideLiteral == resourceIds[0]
        } + firstJumpIndex

        fun endScreenElementByteCode(instructions: List<BuilderInstruction>, index: Int): String {
            return "invoke-static {v${(instructions[index] as OneRegisterInstruction).registerA}}, Lapp/revanced/integrations/patches/HideEndScreenPatch;->hide(Landroid/view/View;)V"
        }
        endScreenElementLayoutCircleMethod.addInstruction(
            circleCheckCastIndex + secondJumpIndex,
            endScreenElementByteCode(endScreenElementLayoutCircleInstructions, circleCheckCastIndex)
        )
        endScreenElementLayoutIconMethod.addInstruction(
            circleCheckCastIndex + secondJumpIndex,
            endScreenElementByteCode(endScreenElementLayoutIconInstructions, iconCheckCastIndex)
        )
        endScreenElementLayoutVideoMethod.addInstruction(
            circleCheckCastIndex + secondJumpIndex,
            endScreenElementByteCode(endScreenElementLayoutVideoInstructions, videoCheckCastIndex)
        )

        return PatchResultSuccess()
    }
}
