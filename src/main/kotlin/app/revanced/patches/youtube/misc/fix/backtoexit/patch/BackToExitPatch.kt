package app.revanced.patches.youtube.misc.fix.backtoexit.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.addInstruction
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint.Companion.resolve
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patches.youtube.misc.fix.backtoexit.annotation.BackToExitCompatibility
import app.revanced.patches.youtube.misc.fix.backtoexit.fingerprints.OnBackPressedFingerprint
import app.revanced.patches.youtube.misc.fix.backtoexit.fingerprints.RecyclerViewScrollingFingerprint
import app.revanced.patches.youtube.misc.fix.backtoexit.fingerprints.RecyclerViewTopScrollingParentFingerprint
import app.revanced.patches.youtube.misc.fix.backtoexit.fingerprints.RecyclerViewTopScrollingFingerprint

@Description("Close the app by tapping the back button from the home feed.")
@BackToExitCompatibility
@Version("0.0.1")
class BackToExitPatch : BytecodePatch(
    listOf(
        OnBackPressedFingerprint,
        RecyclerViewScrollingFingerprint,
        RecyclerViewTopScrollingParentFingerprint,
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {
        with (RecyclerViewScrollingFingerprint.result!!) {
            mutableMethod.addInstruction(
                scanResult.patternScanResult!!.endIndex,
                invokeString("", "onStartScrollView", "")
            )
        }

        RecyclerViewTopScrollingFingerprint.resolve(context, RecyclerViewTopScrollingParentFingerprint.result!!.classDef)

        with (RecyclerViewTopScrollingFingerprint.result!!) {
            mutableMethod.addInstruction(
                scanResult.patternScanResult!!.endIndex + 1,
                invokeString("", "onStopScrollView", "")
            )
        }

        with (OnBackPressedFingerprint.result!!) {
            mutableMethod.addInstruction(
                scanResult.patternScanResult!!.endIndex,
                invokeString("p0", "exitOnBackPressed", "Lcom/google/android/apps/youtube/app/watchwhile/WatchWhileActivity;")
            )
        }

        return PatchResultSuccess()
    }

    private companion object {
        fun invokeString(
            register: String,
            methodName: String,
            methodParams: String
        ) =
            "invoke-static {$register}, Lapp/revanced/integrations/patches/BackToExitPatch;->$methodName($methodParams)V"
    }
}