package app.revanced.patches.youtube.layout.swipecontrols.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.implementation.BytecodeData
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.patch.implementation.BytecodePatch
import app.revanced.patcher.patch.implementation.misc.PatchResult
import app.revanced.patcher.patch.implementation.misc.PatchResultSuccess
import app.revanced.patches.youtube.layout.swipecontrols.annotations.SwipecontrolsCompatibility
import app.revanced.patches.youtube.layout.swipecontrols.signatures.SwipeControlBrightnessEnabledParentSignature
import app.revanced.patches.youtube.layout.swipecontrols.signatures.SwipeControlBrightnessEnabledSignature

@Patch
@Name("swipecontrols")
@Description("Hide Watermark on the page.")
@SwipecontrolsCompatibility
@Version("0.0.1")
class SwipeControlPatch : BytecodePatch(
    listOf(SwipeControlBrightnessEnabledParentSignature)
) {
    override fun execute(data: BytecodeData): PatchResult {
        val result = SwipeControlBrightnessEnabledParentSignature.result!!

        val method = result.findParentMethod(SwipeControlBrightnessEnabledSignature)?.method!!
        method.addInstructions(
            0, """
                invoke-static {}, Lapp/revanced/integrations/patches/VideoSwipeControlsPatch;->isSwipeControlBrightnessEnabled()Z
                move-result v0
                if-eqz v0, :cond_7
                return-void
        """
        )

        return PatchResultSuccess()
    }
}