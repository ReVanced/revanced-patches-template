package app.revanced.patches.youtube.interaction.downloads.bytecode.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.impl.BytecodeData
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.Dependencies
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.patch.impl.BytecodePatch
import app.revanced.patches.youtube.interaction.downloads.annotation.DownloadsCompatibility
import app.revanced.patches.youtube.interaction.downloads.resource.patch.DownloadsResourcePatch
import app.revanced.patches.youtube.misc.playercontrols.bytecode.patch.PlayerControlsBytecodePatch
import app.revanced.patches.youtube.misc.videoid.patch.VideoIdPatch

@Patch
@Name("downloads")
@Dependencies([DownloadsResourcePatch::class, PlayerControlsBytecodePatch::class, VideoIdPatch::class])
@Description("Enables downloading music and videos from YouTube.")
@DownloadsCompatibility
@Version("0.0.1")
class DownloadsBytecodePatch : BytecodePatch() {
    override fun execute(data: BytecodeData): PatchResult {
        val classDescriptor = "Lapp/revanced/integrations/videoplayer/DownloadButton;"

        // initialize the control
        PlayerControlsBytecodePatch
            .initializeControl("$classDescriptor->initializeDownloadButton(Ljava/lang/Object;)V")

        // add code to change the visibility of the control
        PlayerControlsBytecodePatch
            .injectVisibilityCheckCall("$classDescriptor->changeVisibility(Z)V")

        // add code to change to update the video id
        VideoIdPatch.injectCall("Lapp/revanced/integrations/patches/downloads/DownloadsPatch;->setVideoId(Ljava/lang/String;)V")

        return PatchResultSuccess()
    }
}