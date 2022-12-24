package app.revanced.patches.youtube.misc.fix.playback.patch

import app.revanced.extensions.toErrorResult
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.removeInstruction
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patches.shared.settings.preference.impl.StringResource
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.youtube.misc.fix.playback.annotations.FixPlaybackCompatibility
import app.revanced.patches.youtube.misc.fix.playback.fingerprints.VideoEndListenerFingerprint
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch
import app.revanced.patches.youtube.misc.video.information.patch.VideoInformationPatch
import app.revanced.patches.youtube.misc.video.videoid.patch.VideoIdPatch

@DependsOn([
    IntegrationsPatch::class,
    VideoInformationPatch::class, // updates video length and adds method to seek in video, necessary for this patch
    SettingsPatch::class,
    VideoIdPatch::class
])
@Name("fix-playback")
@Description("Fixes the issue with videos not playing when video ads are removed.")
@FixPlaybackCompatibility
@Version("0.0.1")
class FixPlaybackPatch : BytecodePatch(
    listOf(VideoEndListenerFingerprint)
) {
    override fun execute(context: BytecodeContext): PatchResult {
        SettingsPatch.PreferenceScreen.MISC.addPreferences(
            SwitchPreference(
                "revanced_fix_playback",
                StringResource("revanced_fix_playback_title", "Fix video playback issues"),
                false,
                StringResource(
                    "revanced_fix_playback_summary_on",
                    "The fix is enabled"
                ),
                StringResource(
                    "revanced_fix_playback_summary_off",
                    "The fix is disabled"
                )
            )
        )

        val result = VideoEndListenerFingerprint.result ?: return VideoEndListenerFingerprint.toErrorResult()
        val method = result.mutableMethod
        val index = result.scanResult.patternScanResult!!.startIndex

        // Part that calls the Method that recognizes that the video is over
        method.removeInstruction(index)

        // If you don't remove this method call, video will stop as soon as it starts
        method.removeInstruction(index - 1)

        // If a new video loads, fix the playback issue
        VideoIdPatch.injectCall("Lapp/revanced/integrations/patches/FixPlaybackPatch;->newVideoLoaded(Ljava/lang/String;)V")

        return PatchResultSuccess()
    }
}
