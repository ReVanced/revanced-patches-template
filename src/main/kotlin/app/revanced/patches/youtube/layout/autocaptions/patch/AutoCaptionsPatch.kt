package app.revanced.patches.youtube.layout.autocaptions.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.youtube.layout.autocaptions.fingerprints.StartVideoInformerFingerprint
import app.revanced.patches.youtube.layout.autocaptions.fingerprints.SubtitleButtonControllerFingerprint
import app.revanced.patches.youtube.layout.autocaptions.fingerprints.SubtitleTrackFingerprint
import app.revanced.patches.youtube.layout.buttons.captions.annotations.HideCaptionsButtonCompatibility
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
import app.revanced.patches.youtube.misc.settings.bytecode.patch.YouTubeSettingsPatch

@Patch
@DependsOn([IntegrationsPatch::class, YouTubeSettingsPatch::class])
@Name("disable-auto-captions")
@Description("Disable forced captions from being automatically enabled.")
@HideCaptionsButtonCompatibility
@Version("0.0.1")
class AutoCaptionsPatch : BytecodePatch(
    listOf(
        StartVideoInformerFingerprint, SubtitleButtonControllerFingerprint, SubtitleTrackFingerprint
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {
        YouTubeSettingsPatch.PreferenceScreen.LAYOUT.addPreferences(
            SwitchPreference(
                "revanced_auto_captions",
                "revanced_auto_captions_title",
                "revanced_auto_captions_summary_on",
                "revanced_auto_captions_summary_off"
            )
        )

        val startVideoInformerMethod = StartVideoInformerFingerprint.result!!.mutableMethod

        startVideoInformerMethod.addInstructions(
            0, """
            const/4 v0, 0x0
            sput-boolean v0, Lapp/revanced/integrations/patches/DisableAutoCaptionsPatch;->captionsButtonDisabled:Z
        """
        )

        val subtitleButtonControllerMethod = SubtitleButtonControllerFingerprint.result!!.mutableMethod

        subtitleButtonControllerMethod.addInstructions(
            0, """
            const/4 v0, 0x1
            sput-boolean v0, Lapp/revanced/integrations/patches/DisableAutoCaptionsPatch;->captionsButtonDisabled:Z
        """
        )

        val subtitleTrackMethod = SubtitleTrackFingerprint.result!!.mutableMethod

        subtitleTrackMethod.addInstructions(
            0, """
            invoke-static {}, Lapp/revanced/integrations/patches/DisableAutoCaptionsPatch;->autoCaptionsEnabled()Z
            move-result v0
            if-eqz v0, :auto_captions_enabled
            sget-boolean v0, Lapp/revanced/integrations/patches/DisableAutoCaptionsPatch;->captionsButtonDisabled:Z
            if-nez v0, :auto_captions_enabled
            const/4 v0, 0x1
            return v0
            :auto_captions_enabled
            nop
        """
        )

        return PatchResultSuccess()
    }
}
