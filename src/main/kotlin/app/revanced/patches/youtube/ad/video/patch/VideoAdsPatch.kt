package app.revanced.patches.youtube.ad.video.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.BytecodeContext
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.extensions.instruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.util.smali.ExternalLabel
import app.revanced.patches.youtube.ad.video.annotations.VideoAdsCompatibility
import app.revanced.patches.youtube.ad.video.fingerprints.LoadAdsFingerprint
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch
import app.revanced.patches.youtube.misc.settings.framework.components.impl.StringResource
import app.revanced.patches.youtube.misc.settings.framework.components.impl.SwitchPreference

@Patch
@DependsOn([IntegrationsPatch::class, SettingsPatch::class])
@Name("video-ads")
@Description("Removes ads in the video player.")
@VideoAdsCompatibility
@Version("0.0.1")
class VideoAdsPatch : BytecodePatch(
    listOf(
        LoadAdsFingerprint
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {
        SettingsPatch.PreferenceScreen.ADS.addPreferences(
            SwitchPreference(
                "revanced_video_ads_removal",
                StringResource("revanced_video_ads_removal_title", "Hide video ads"),
                true,
                StringResource("revanced_video_ads_removal_summary_on", "Video ads are hidden"),
                StringResource("revanced_video_ads_removal_summary_off", "Video ads are shown")
            )
        )

        with(LoadAdsFingerprint.result!!) {
            val insertIndex = scanResult.patternScanResult!!.startIndex
            with(mutableMethod) {
                addInstructions(
                    insertIndex,
                    """ 
                            invoke-static { }, Lapp/revanced/integrations/patches/VideoAdsPatch;->shouldShowAds()Z
                            move-result v4
                            if-eqz v4, :show_video_ads
                            return-object v3
                         """,
                    listOf(ExternalLabel("show_video_ads", instruction(insertIndex)))
                )
            }
        }

        return PatchResultSuccess()
    }
}
