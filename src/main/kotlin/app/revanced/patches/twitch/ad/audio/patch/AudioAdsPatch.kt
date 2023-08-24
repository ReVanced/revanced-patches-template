package app.revanced.patches.twitch.ad.audio.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.util.smali.ExternalLabel
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.twitch.ad.audio.annotations.AudioAdsCompatibility
import app.revanced.patches.twitch.ad.audio.fingerprints.AudioAdsPresenterPlayFingerprint
import app.revanced.patches.twitch.misc.integrations.patch.TwitchIntegrationsPatch
import app.revanced.patches.twitch.misc.settings.bytecode.patch.TwitchSettingsPatch

@Patch
@DependsOn([TwitchIntegrationsPatch::class, TwitchSettingsPatch::class])
@Name("Block audio ads")
@Description("Blocks audio ads in streams and VODs.")
@AudioAdsCompatibility
class AudioAdsPatch : BytecodePatch(
    listOf(AudioAdsPresenterPlayFingerprint)
) {
    override fun execute(context: BytecodeContext) {
        // Block playAds call
        with(AudioAdsPresenterPlayFingerprint.result!!) {
            mutableMethod.addInstructionsWithLabels(
                0,
                """
                    invoke-static { }, Lapp/revanced/twitch/patches/AudioAdsPatch;->shouldBlockAudioAds()Z
                    move-result v0
                    if-eqz v0, :show_audio_ads
                    return-void
                """,
                ExternalLabel("show_audio_ads", mutableMethod.getInstruction(0))
            )
        }

        TwitchSettingsPatch.PreferenceScreen.ADS.CLIENT_SIDE.addPreferences(
            SwitchPreference(
                "revanced_block_audio_ads",
                "revanced_block_audio_ads",
                "revanced_block_audio_ads_on",
                "revanced_block_audio_ads_off",
                default = true,
            )
        )
    }
}
