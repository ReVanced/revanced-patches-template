package app.revanced.patches.youtube.misc.bottomsheet.hook.patch

import app.revanced.patcher.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.util.resources.ResourceUtils.resourceIdOf

class BottomSheetHookResourcePatch : ResourcePatch {
    override suspend fun execute(context: ResourceContext) {
        bottomSheetMargins = context.resourceIdOf("dimen", "bottom_sheet_margins")
    }

    internal companion object {
        var bottomSheetMargins = -1L
    }
}