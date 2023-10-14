package app.revanced.patches.tumblr.live

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patches.tumblr.featureflags.OverrideFeatureFlagsPatch
import app.revanced.patches.tumblr.timelinefilter.TimelineFilterPatch

@Patch(
    name = "Disable Tumblr Live",
    description = "Disable the Tumblr Live tab button and dashboard carousel.",
    dependencies = [OverrideFeatureFlagsPatch::class, TimelineFilterPatch::class],
    compatiblePackages = [CompatiblePackage("com.tumblr")]
)
@Suppress("unused")
object DisableTumblrLivePatch : BytecodePatch() {
    override fun execute(context: BytecodeContext) {
        // Hide the LIVE_MARQUEE timeline element that appears in the feed
        // Called "live_marquee" in api response
        TimelineFilterPatch.addObjectTypeFilter("LIVE_MARQUEE")

        // Hide the Tab button for Tumblr Live by forcing the feature flag to false
        OverrideFeatureFlagsPatch.addOverride("liveStreaming", "false")
    }
}