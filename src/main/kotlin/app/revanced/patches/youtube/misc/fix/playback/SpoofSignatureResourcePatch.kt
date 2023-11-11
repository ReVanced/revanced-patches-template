package app.revanced.patches.youtube.misc.fix.playback

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patches.shared.mapping.misc.ResourceMappingPatch
import app.revanced.patches.youtube.misc.settings.SettingsPatch

@Patch(dependencies = [SettingsPatch::class, ResourceMappingPatch::class])
object SpoofSignatureResourcePatch : ResourcePatch() {
    internal var scrubbedPreviewThumbnailResourceId: Long = -1

    override fun execute(context: ResourceContext) {
        scrubbedPreviewThumbnailResourceId = ResourceMappingPatch.resourceMappings.single {
            it.type == "id" && it.name == "thumbnail"
        }.id
    }
}
