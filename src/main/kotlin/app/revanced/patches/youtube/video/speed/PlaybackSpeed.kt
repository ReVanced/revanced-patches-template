package app.revanced.patches.youtube.video.speed

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patches.youtube.video.speed.custom.CustomPlaybackSpeedPatch
import app.revanced.patches.youtube.video.speed.remember.RememberPlaybackSpeedPatch

@Patch(
    name = "Playback speed",
    description = "Adds custom playback speeds and ability to remember the playback speed you chose in the video playback speed flyout.",
    dependencies = [CustomPlaybackSpeedPatch::class, RememberPlaybackSpeedPatch::class]
)
@PlaybackSpeedCompatibility
@Suppress("unused")
object PlaybackSpeed : BytecodePatch() {
    override fun execute(context: BytecodeContext) { // All patches this patch depends on succeed.
    }
}
