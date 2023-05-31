package app.revanced.patches.youtube.video.speed

import app.revanced.patcher.BytecodeContext
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.youtube.video.speed.custom.patch.CustomVideoSpeedPatch
import app.revanced.patches.youtube.video.speed.remember.patch.RememberPlaybackSpeedPatch

@Patch
@Name("video-speed")
@Description("Adds custom video speeds and ability to remember the playback speed you chose in the video playback speed flyout.")
@DependsOn([CustomVideoSpeedPatch::class, RememberPlaybackSpeedPatch::class])
@VideoSpeedCompatibility
@Version("0.0.1")
class VideoSpeed : BytecodePatch() {
    override suspend fun execute(context: BytecodeContext) {
        // All patches this patch depends on succeed.
    }
}
