package app.revanced.patches.youtube.ad.video.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.impl.BytecodeData
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.fingerprint.method.utils.MethodFingerprintUtils.resolve
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.Dependencies
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.patch.impl.BytecodePatch
import app.revanced.patches.youtube.ad.video.annotations.VideoAdsCompatibility
import app.revanced.patches.youtube.ad.video.fingerprints.ShowVideoAdsConstructorFingerprint
import app.revanced.patches.youtube.ad.video.fingerprints.ShowVideoAdsFingerprint
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch
import app.revanced.patches.youtube.misc.settings.framework.components.impl.PreferenceScreen
import app.revanced.patches.youtube.misc.settings.framework.components.impl.StringResource
import app.revanced.patches.youtube.misc.settings.framework.components.impl.SwitchPreference

@Patch
@Dependencies([IntegrationsPatch::class, SettingsPatch::class])
@Name("video-ads")
@Description("Removes ads in the video player.")
@VideoAdsCompatibility
@Version("0.0.1")
class VideoAdsPatch : BytecodePatch(
    listOf(
        ShowVideoAdsConstructorFingerprint
    )
) {
    override fun execute(data: BytecodeData): PatchResult {
        SettingsPatch.addPreferenceScreen(
            PreferenceScreen(
                "revanced_video_ads", StringResource("revanced_video_ads_title", "Video Ads"), listOf(
                    SwitchPreference(
                        "revanced_video_ads_enabled",
                        StringResource("revanced_video_ads_enabled_title", "Show video ads"),
                        true,
                        StringResource("revanced_video_ads_enabled_summary_on", "Video ads are enabled."),
                        StringResource("revanced_video_ads_enabled_summary_off", "Video ads are disabled.")
                    ),
                )
            )
        )

        ShowVideoAdsFingerprint.resolve(data, ShowVideoAdsConstructorFingerprint.result!!.classDef)

        // Override the parameter by calling shouldShowAds and setting the parameter to the result
        ShowVideoAdsFingerprint.result!!.mutableMethod.addInstructions(
            0, """
                invoke-static { }, Lapp/revanced/integrations/patches/VideoAdsPatch;->shouldShowAds()Z
                move-result v1
            """
        )

        return PatchResultSuccess()
    }
}