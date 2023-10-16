package app.revanced.patches.twitch.ad.audio

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patcher.util.smali.ExternalLabel
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.twitch.ad.audio.fingerprints.AudioAdsPresenterPlayFingerprint
import app.revanced.patches.twitch.misc.integrations.IntegrationsPatch
import app.revanced.patches.twitch.misc.settings.SettingsPatch

@Patch(
    name = "Block audio ads",
    description = "Blocks audio ads in streams and VODs.",
    dependencies = [IntegrationsPatch::class, SettingsPatch::class],
    compatiblePackages = [CompatiblePackage("tv.twitch.android.app", ["15.4.1", "16.1.0"])],
)
@Suppress("unused")
object AudioAdsPatch : BytecodePatch(
    setOf(AudioAdsPresenterPlayFingerprint)
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

        SettingsPatch.includePatchStrings("AudioAds")
        SettingsPatch.PreferenceScreen.ADS.CLIENT_SIDE.addPreferences(
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
