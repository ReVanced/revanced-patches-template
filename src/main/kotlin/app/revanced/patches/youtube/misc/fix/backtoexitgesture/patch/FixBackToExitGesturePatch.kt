package app.revanced.patches.youtube.misc.fix.backtoexitgesture.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.addInstruction
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint.Companion.resolve
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patches.youtube.misc.fix.backtoexitgesture.annotation.FixBackToExitGestureCompatibility
import app.revanced.patches.youtube.misc.fix.backtoexitgesture.fingerprints.OnBackPressedFingerprint
import app.revanced.patches.youtube.misc.fix.backtoexitgesture.fingerprints.RecyclerViewScrollingFingerprint
import app.revanced.patches.youtube.misc.fix.backtoexitgesture.fingerprints.RecyclerViewTopScrollingParentFingerprint
import app.revanced.patches.youtube.misc.fix.backtoexitgesture.fingerprints.RecyclerViewTopScrollingFingerprint

@Description("Closes the app by tapping the back button from the home feed.")
@FixBackToExitGestureCompatibility
@Version("0.0.1")
class FixBackToExitGesturePatch : BytecodePatch(
    listOf(
        OnBackPressedFingerprint,
        RecyclerViewScrollingFingerprint,
        RecyclerViewTopScrollingParentFingerprint,
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {
        val recyclerViewScrollingFingerprint = RecyclerViewScrollingFingerprint.result!!
        recyclerViewScrollingFingerprint.mutableMethod.addInstruction(
            recyclerViewScrollingFingerprint.scanResult.patternScanResult!!.endIndex,
            injectCall("", "onStartScrollView", "")
        )

        RecyclerViewTopScrollingFingerprint.resolve(context, RecyclerViewTopScrollingParentFingerprint.result!!.classDef)

        val recyclerViewTopScrollingFingerprint = RecyclerViewTopScrollingFingerprint.result!!
        println(recyclerViewTopScrollingFingerprint)
        recyclerViewTopScrollingFingerprint.mutableMethod.addInstruction(
            recyclerViewTopScrollingFingerprint.scanResult.patternScanResult!!.endIndex + 1,
            injectCall("", "onStopScrollView", "")
        )

        val onBackPressedFingerprint = RecyclerViewScrollingFingerprint.result!!
        onBackPressedFingerprint.mutableMethod.addInstruction(
            onBackPressedFingerprint.scanResult.patternScanResult!!.endIndex,
            injectCall("p0", "exitOnBackPressed", "Lcom/google/android/apps/youtube/app/watchwhile/WatchWhileActivity;")
        )

        return PatchResultSuccess()
    }

    private companion object {
        fun injectCall(
            register: String,
            methodName: String,
            methodParams: String
        ) =
            "invoke-static {$register}, Lapp/revanced/integrations/patches/BackToExitPatch;->$methodName($methodParams)V"
    }
}