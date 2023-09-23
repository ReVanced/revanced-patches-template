package app.revanced.patches.tumblr.ads

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patches.tumblr.timelinefilter.TimelineFilterPatch

@Patch(
    name = "Disable dashboard ads",
    description = "Disables ads in the dashboard.",
    compatiblePackages = [CompatiblePackage("com.tumblr")],
    dependencies = [TimelineFilterPatch::class]
)
@Suppress("unused")
object DisableDashboardAds : BytecodePatch() {
    override fun execute(context: BytecodeContext)  {
        // Called "client_side_ad_waterfall" in api response
        TimelineFilterPatch.addObjectTypeFilter("CLIENT_SIDE_MEDIATION")
        // Called "backfill_ad" in api response
        TimelineFilterPatch.addObjectTypeFilter("GEMINI_AD")

        // The below object types weren't actually spotted in the wild in testing, but they are valid Object types
        // and their names clearly indicate that they are ads, so we just block them anyway,
        // just in case they will be used in the future.

        // Called "nimbus_ad" in api response
        TimelineFilterPatch.addObjectTypeFilter("NIMBUS_AD")
        // Called "client_side_ad" in api response
        TimelineFilterPatch.addObjectTypeFilter("CLIENT_SIDE_AD")
        // Called "display_io_interscroller" in api response
        TimelineFilterPatch.addObjectTypeFilter("DISPLAY_IO_INTERSCROLLER_AD")
        // Called "display_io_headline_video" in api response
        TimelineFilterPatch.addObjectTypeFilter("DISPLAY_IO_HEADLINE_VIDEO_AD")
        // Called "facebook_biddable_sdk_ad" in api response
        TimelineFilterPatch.addObjectTypeFilter("FACEBOOK_BIDDAABLE")
        // Called "google_native_ad" in api response
        TimelineFilterPatch.addObjectTypeFilter("GOOGLE_NATIVE")
    }
}