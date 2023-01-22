package app.revanced.patches.youtube.misc.shorts.resource

import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patches.shared.mapping.misc.patch.ResourceMappingPatch

@DependsOn([ResourceMappingPatch::class])
@Version("0.0.1")
class ShortsDetectionResourcePatch : ResourcePatch {
    internal companion object {
        var reelButtonGroupResourceId: Long = 0
            private set
    }

    override fun execute(context: ResourceContext): PatchResult {
        reelButtonGroupResourceId = ResourceMappingPatch.resourceMappings.single {
            it.type == "id" && it.name == "reel_persistent_edu_button_group"
        }.id

        return PatchResultSuccess()
    }
}