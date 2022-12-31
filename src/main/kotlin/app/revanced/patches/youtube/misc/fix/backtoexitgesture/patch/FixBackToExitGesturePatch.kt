package app.revanced.patches.youtube.misc.fix.backtoexitgesture.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.addInstruction
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
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
        MethodPatch(
            RecyclerViewScrollingFingerprint,
            "",
            "onScrollingViews",
            ""
        ).injectCall()

        RecyclerViewTopScrollingFingerprint.resolve(context, RecyclerViewTopScrollingParentFingerprint.result!!.classDef)

        MethodPatch(
            RecyclerViewTopScrollingFingerprint,
            "",
            "onTopView",
            ""
        ).injectCall()

        MethodPatch(
            RecyclerViewScrollingFingerprint,
            "p0",
            "onBackPressed",
            "Lcom/google/android/apps/youtube/app/watchwhile/WatchWhileActivity;"
        ).injectCall()

        return PatchResultSuccess()
    }

    private companion object {
        data class MethodPatch(
            val fingerprint: MethodFingerprint,
            val register: String,
            val methodName: String,
            val methodParams: String
        ) {
            val fingerprintResult = fingerprint.result!!
            val fingerprintMethod = fingerprintResult.mutableMethod
            val patchLineIndex = fingerprintResult.scanResult.patternScanResult!!.endIndex

            fun injectCall() {
                fingerprintMethod.addInstruction(
                    patchLineIndex,
                    "invoke-static {$register}, Lapp/revanced/integrations/patches/FixBackToExitGesturePatch;->$methodName($methodParams)V"
                )
            }
        }
    }
}