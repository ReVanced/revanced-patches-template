package app.revanced.patches.youtube.layout.swipecontrols.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.impl.BytecodeData
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.fingerprint.method.utils.MethodFingerprintUtils.resolve
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultError
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.patch.impl.BytecodePatch
import app.revanced.patches.youtube.layout.swipecontrols.annotations.SwipecontrolsCompatibility
import app.revanced.patches.youtube.layout.swipecontrols.fingerprints.SwipeControlBrightnessEnabledParentFingerprint
import app.revanced.patches.youtube.layout.swipecontrols.fingerprints.SwipeControlBrightnessEnabledFingerprint
import app.revanced.patches.youtube.layout.swipecontrols.fingerprints.YouTubePlayerOverlaysLayoutConstructorFingerprint

@Patch
@Name("swipeControls")
@Description("Hide Watermark on the page.")
@SwipecontrolsCompatibility
@Version("0.0.1")
class SwipeControlPatch : BytecodePatch(
    listOf(SwipeControlBrightnessEnabledParentFingerprint)
) {
    override fun execute(data: BytecodeData): PatchResult {
        //runSwipeControlsEnabled(data)
        addToFirstConstructor(data)
        return PatchResultSuccess()
    }

    private fun runSwipeControlsEnabled(data: BytecodeData): PatchResult {
        SwipeControlBrightnessEnabledFingerprint.resolve(
            data,
            SwipeControlBrightnessEnabledParentFingerprint.result!!.classDef
        )

        val result = SwipeControlBrightnessEnabledFingerprint.result
            ?: return PatchResultError("Required parent method could not be found.")

        result.mutableMethod.addInstructions(
            0, """
                invoke-static {}, Lapp/revanced/integrations/patches/VideoSwipeControlsPatch;->isSwipeControlBrightnessEnabled()Z
                move-result v0
                if-eqz v0, :cond_7
                return-void
        """
        )
        return PatchResultSuccess()
    }

    private fun addToFirstConstructor(data: BytecodeData): PatchResult {
        //result is first constructor method in YouTubePlayerOverlaysLayout class
        val result = YouTubePlayerOverlaysLayoutConstructorFingerprint.result
            ?: return PatchResultError("Required parent method could not be found.")

        result.mutableMethod.addInstructions(
            result.method.implementation!!.instructions.count() - 1, """
            invoke-virtual {p0}, Lcom/google/android/apps/youtube/app/common/player/overlay/YouTubePlayerOverlaysLayout;->getContext()Landroid/content/Context;
            move-result-object v0
            invoke-virtual {p0}, Lcom/google/android/apps/youtube/app/common/player/overlay/YouTubePlayerOverlaysLayout;->getContext()Landroid/content/Context;
            move-result-object v1
            invoke-static {v1}, Landroid/view/ViewConfiguration;->get(Landroid/content/Context;)Landroid/view/ViewConfiguration;
            move-result-object v1
            invoke-static {v0, p0, v1}, Lapp/revanced/integrations/videoswipecontrols/SwipeControlAPI;->InitializeFensterController(Landroid/content/Context;Landroid/view/ViewGroup;Landroid/view/ViewConfiguration;)V
        """
        )

        return PatchResultSuccess()
    }
}