package app.revanced.patches.youtube.layout.sponsorblock.resource.patch

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patches.shared.mapping.misc.patch.ResourceMappingPatch
import app.revanced.patches.youtube.layout.sponsorblock.annotations.SponsorBlockCompatibility

@Name("shorts-playback-detection")
@SponsorBlockCompatibility
@DependsOn([ResourceMappingPatch::class])
@Version("0.0.1")
class ShortsPlaybackDetection : ResourcePatch {
    companion object {
        internal var reelButtonGroupResourceId: Long = 0
    }

    override fun execute(context: ResourceContext): PatchResult {
        reelButtonGroupResourceId = ResourceMappingPatch.resourceMappings.single {
            it.type == "id" && it.name == "reel_persistent_edu_button_group"
        }.id

        return PatchResultSuccess()
    }
}