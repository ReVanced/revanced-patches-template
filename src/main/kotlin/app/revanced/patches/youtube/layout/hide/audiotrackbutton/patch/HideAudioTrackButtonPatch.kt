package app.revanced.patches.youtube.layout.hide.audiotrackbutton.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.shared.mapping.misc.patch.ResourceMappingPatch
import app.revanced.patches.shared.settings.preference.impl.StringResource
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.youtube.layout.hide.audiotrackbutton.annotations.HideAudioTrackButtonCompatibility
import app.revanced.patches.youtube.misc.litho.filter.patch.LithoFilterPatch
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch

@Patch
@DependsOn([ResourceMappingPatch::class, LithoFilterPatch::class])
@Name("hide-audio-track-button")
@Description("Hides the audio track button on the video player overlay.")
@HideAudioTrackButtonCompatibility
@Version("0.0.1")
class HideAudioTrackButtonPatch : ResourcePatch {
    override fun execute(context: ResourceContext): PatchResult {
        SettingsPatch.PreferenceScreen.LAYOUT.addPreferences(
            SwitchPreference(
                "revanced_hide_audio_track_button",
                StringResource("revanced_hide_audio_track_button_title", "Hide audio track button"),
                false,
                StringResource("revanced_hide_audio_track_button_on", "Audio track button is hidden"),
                StringResource("revanced_hide_audio_track_button_off", "Audio track button is shown")
            ),
        )
        return PatchResultSuccess()
    }
}
