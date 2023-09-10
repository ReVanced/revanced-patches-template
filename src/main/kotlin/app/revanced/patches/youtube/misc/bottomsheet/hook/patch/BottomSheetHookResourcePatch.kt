package app.revanced.patches.youtube.misc.bottomsheet.hook

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.PatchException
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patches.shared.mapping.misc.patch.ResourceMappingPatch

@Patch(
    dependencies = [ResourceMappingPatch::class]
)
object BottomSheetHookResourcePatch : ResourcePatch {
    override fun execute(context: ResourceContext) {
        bottomSheetMargins =  ResourceMappingPatch.resourceMappings.find { it.name == "bottom_sheet_margins" }?.id
            ?: throw PatchException("Could not find resource")
    }

    internal companion object {
        var bottomSheetMargins = -1L
    }
}