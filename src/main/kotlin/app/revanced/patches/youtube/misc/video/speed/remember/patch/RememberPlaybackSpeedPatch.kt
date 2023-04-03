package app.revanced.patches.youtube.misc.video.speed.remember.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.patch.*
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.shared.settings.preference.impl.StringResource
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch
import app.revanced.patches.youtube.misc.video.speed.current.patch.CurrentPlaybackSpeedPatch
import app.revanced.patches.youtube.misc.video.speed.remember.annotation.RememberPlaybackSpeedCompatibility
import app.revanced.patches.youtube.misc.video.videoid.patch.VideoIdPatch

@Patch
@Name("remember-playback-speed")
@Description("Adds the ability to remember the playback speed you chose in the video playback speed flyout.")
@DependsOn([IntegrationsPatch::class, SettingsPatch::class, VideoIdPatch::class, CurrentPlaybackSpeedPatch::class])
@RememberPlaybackSpeedCompatibility
@Version("0.0.1")
class RememberPlaybackSpeedPatch : BytecodePatch() {
    override fun execute(context: BytecodeContext): PatchResult {
        SettingsPatch.PreferenceScreen.MISC.addPreferences(
            SwitchPreference(
                "revanced_remember_playback_speed_last_selected",
                StringResource(
                    "revanced_remember_playback_speed_last_selected_title",
                    "Remember playback speed changes"
                ),
                true,
                StringResource(
                    "revanced_remember_playback_speed_last_selected_summary_on",
                    "Playback speed changes apply to all videos"
                ),
                StringResource(
                    "revanced_remember_playback_speed_last_selected_summary_off",
                    "Playback speed changes only apply to the current video"
                )
            )
        )

        VideoIdPatch.injectCall("${INTEGRATIONS_CLASS_DESCRIPTOR}->newVideoLoaded(Ljava/lang/String;)V")

        CurrentPlaybackSpeedPatch.injectVideoSpeedSelectedByUser(
            "$INTEGRATIONS_CLASS_DESCRIPTOR->userSelectedPlaybackSpeed(F)V")

        CurrentPlaybackSpeedPatch.injectVideoSpeedOverride(
            "$INTEGRATIONS_CLASS_DESCRIPTOR->getVideoSpeedOverride()F")

        return PatchResultSuccess()
    }

    private companion object {
        const val INTEGRATIONS_CLASS_DESCRIPTOR =
            "Lapp/revanced/integrations/patches/playback/speed/RememberPlaybackSpeedPatch;"
    }
}