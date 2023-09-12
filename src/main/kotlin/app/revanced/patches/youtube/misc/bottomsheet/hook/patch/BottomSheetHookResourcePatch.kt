package app.revanced.patches.youtube.misc.bottomsheet.hook.patch

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.PatchException
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patches.shared.mapping.misc.ResourceMappingPatch

@Patch(
    name = "Remove root detection",
    description = "Removes the check for root permissions.",
    dependencies = [ResourceMappingPatch::class]
)
@Suppress("unused")
object BottomSheetHookResourcePatch : ResourcePatch() {
    internal var bottomSheetMargins = -1L

    override fun execute(context: ResourceContext) {
        bottomSheetMargins =  ResourceMappingPatch.resourceMappings.find { it.name == "bottom_sheet_margins" }?.id
            ?: throw PatchException("Could not find resource")
    }
}