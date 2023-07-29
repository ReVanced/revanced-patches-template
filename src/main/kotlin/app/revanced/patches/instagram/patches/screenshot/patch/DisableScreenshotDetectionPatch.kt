package app.revanced.patches.instagram.patches.screenshot.patch

import app.revanced.extensions.toErrorResult
import app.revanced.patcher.annotation.*
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.replaceInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.instagram.patches.screenshot.fingerprints.ScreenshotDetectionObserverFingerprint

@Patch
@Name("Disable screenshot detection")
@Description("Prevents the app from being able to detect a screenshot being taken.")
@Compatibility([Package("com.instagram.android")])
@Version("0.0.1")
class DisableScreenshotDetectionPatch : BytecodePatch(
    listOf(ScreenshotDetectionObserverFingerprint)
) {
    override fun execute(context: BytecodeContext): PatchResult {
        ScreenshotDetectionObserverFingerprint.result?.mutableMethod?.apply {
            this.replaceInstruction(0, "return-void")
        } ?: return ScreenshotDetectionObserverFingerprint.toErrorResult()

        return PatchResultSuccess()
    }
}