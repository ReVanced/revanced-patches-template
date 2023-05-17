package app.revanced.patches.youtube.interaction.copyvideourl.bytecode.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.youtube.interaction.copyvideourl.annotation.CopyVideoUrlCompatibility
import app.revanced.patches.youtube.interaction.copyvideourl.resource.patch.CopyVideoUrlResourcePatch
import app.revanced.patches.youtube.misc.playercontrols.bytecode.patch.PlayerControlsBytecodePatch
import app.revanced.patches.youtube.video.information.patch.VideoInformationPatch

@Patch
@Name("copy-video-url")
@Description("Adds a player button to copy the video link.")
@DependsOn([
    CopyVideoUrlResourcePatch::class,
    PlayerControlsBytecodePatch::class,
    VideoInformationPatch::class
])
@CopyVideoUrlCompatibility
@Version("0.0.1")
class CopyVideoUrlBytecodePatch : BytecodePatch() {
    private companion object {
        const val BUTTON_DESCRIPTOR = "Lapp/revanced/integrations/videoplayer/CopyVideoUrlButton;"
    }

    override fun execute(context: BytecodeContext): PatchResult {

        // Initialize button and inject visibility control
        PlayerControlsBytecodePatch.initializeControl(
            "$BUTTON_DESCRIPTOR->initializeButton(Landroid/view/View;)V")
        PlayerControlsBytecodePatch.injectVisibilityCheckCall(
            "$BUTTON_DESCRIPTOR->changeVisibility(Z)V")

        return PatchResultSuccess()
    }
}