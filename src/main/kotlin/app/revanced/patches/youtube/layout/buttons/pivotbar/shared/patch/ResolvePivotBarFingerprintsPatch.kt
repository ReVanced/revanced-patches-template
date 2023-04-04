package app.revanced.patches.youtube.layout.buttons.pivotbar.shared.patch

import app.revanced.extensions.toErrorResult
import app.revanced.patcher.BytecodeContext
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint.Companion.resolve
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patches.youtube.layout.buttons.pivotbar.shared.annotations.PivotBarCompatibility
import app.revanced.patches.youtube.layout.buttons.pivotbar.shared.fingerprints.InitializeButtonsFingerprint
import app.revanced.patches.youtube.layout.buttons.pivotbar.shared.fingerprints.PivotBarConstructorFingerprint

@PivotBarCompatibility
@Description("Resolves necessary fingerprints.")
@Version("0.0.1")
class ResolvePivotBarFingerprintsPatch : BytecodePatch(
    listOf(PivotBarConstructorFingerprint)
) {
    internal companion object {
        var imageOnlyTabResourceId: Long = -1
    }

    override fun execute(context: BytecodeContext): PatchResult {
        // imageOnlyTabResourceId is used in InitializeButtonsFingerprint fingerprint
        imageOnlyTabResourceId = context.resourceIdOf("layout", "image_only_tab")

        PivotBarConstructorFingerprint.result?.let {
            // Resolve InitializeButtonsFingerprint on the class of the method
            // which PivotBarConstructorFingerprint resolved to
            if (!InitializeButtonsFingerprint.resolve(
                    context,
                    it.classDef
                )
            ) return InitializeButtonsFingerprint.toErrorResult()
        } ?: return PivotBarConstructorFingerprint.toErrorResult()
        return PatchResult.Success
    }
}