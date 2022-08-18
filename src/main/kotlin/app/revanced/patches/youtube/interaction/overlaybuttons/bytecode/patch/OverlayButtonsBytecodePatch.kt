package app.revanced.patches.youtube.interaction.overlaybuttons.bytecode.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.impl.BytecodeData
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.patch.impl.BytecodePatch
import app.revanced.patches.youtube.interaction.overlaybuttons.annotation.OverlayButtonsCompatibility
import app.revanced.patches.youtube.interaction.overlaybuttons.resource.patch.OverlayButtonsResourcePatch
import app.revanced.patches.youtube.misc.playercontrols.bytecode.patch.PlayerControlsBytecodePatch
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch
import app.revanced.patches.youtube.misc.autorepeat.patch.AutoRepeatPatch
import app.revanced.patches.youtube.misc.mapping.patch.ResourceIdMappingProviderResourcePatch
import app.revanced.patches.youtube.misc.videoid.patch.VideoIdPatch

@Patch
@Name("overlay-buttons")
@DependsOn(
    dependencies = [AutoRepeatPatch::class, SettingsPatch::class, IntegrationsPatch::class, ResourceIdMappingProviderResourcePatch::class, OverlayButtonsResourcePatch::class, PlayerControlsBytecodePatch::class, VideoIdPatch::class]
)
@Description("Enables buttons overlay.")
@OverlayButtonsCompatibility
@Version("0.0.1")
class ButtonsBytecodePatch : BytecodePatch() {
    override fun execute(data: BytecodeData): PatchResult {
        val integrationsPackage = "app/revanced/integrations"
        val CopyWithTimeStamp = "L$integrationsPackage/videoplayer/CopyWithTimeStamp;"
        val Copy = "L$integrationsPackage/videoplayer/Copy;"
        val AutoRepeat = "L$integrationsPackage/videoplayer/AutoRepeat;"

        /*
        initialize the control
         */

        val initializeCopyWithTimeStamp = "$CopyWithTimeStamp->initializeCopyButtonWithTimeStamp(Ljava/lang/Object;)V"
        val initializeCopy = "$Copy->initializeCopyButton(Ljava/lang/Object;)V"
        val initializeAutoRepeat = "$AutoRepeat->initializeAutoRepeat(Ljava/lang/Object;)V"
        PlayerControlsBytecodePatch.initializeControl(initializeCopyWithTimeStamp)
        PlayerControlsBytecodePatch.initializeControl(initializeCopy)
        PlayerControlsBytecodePatch.initializeControl(initializeAutoRepeat)

        /*
         add code to change the visibility of the control
         */

        val changeVisibilityCopyWithTimeStamp = "$CopyWithTimeStamp->changeVisibility(Z)V"
        val changeVisibilityCopy = "$Copy->changeVisibility(Z)V"
        val changeVisibilityAutoRepeat = "$AutoRepeat->changeVisibility(Z)V"
        PlayerControlsBytecodePatch.injectVisibilityCheckCall(changeVisibilityCopyWithTimeStamp)
        PlayerControlsBytecodePatch.injectVisibilityCheckCall(changeVisibilityCopy)
        PlayerControlsBytecodePatch.injectVisibilityCheckCall(changeVisibilityAutoRepeat)

        return PatchResultSuccess()
    }
}