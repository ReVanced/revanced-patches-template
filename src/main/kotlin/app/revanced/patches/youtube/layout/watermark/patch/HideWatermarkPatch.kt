package app.revanced.patches.youtube.layout.watermark.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.implementation.BytecodeData
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.patch.implementation.BytecodePatch
import app.revanced.patcher.patch.implementation.misc.PatchResult
import app.revanced.patcher.patch.implementation.misc.PatchResultSuccess
import app.revanced.patches.youtube.layout.watermark.annotations.HideWatermarkCompatibility
import app.revanced.patches.youtube.layout.watermark.signatures.HideWatermarkParentSignature
import app.revanced.patches.youtube.layout.watermark.signatures.HideWatermarkSignature

@Name("hide-watermark")
@Description("Hide Watermark on the page.")
@HideWatermarkCompatibility
@Version("0.0.1")
class HideWatermarkPatch : BytecodePatch(
    listOf(
        HideWatermarkParentSignature
    )
) {
    override fun execute(data: BytecodeData): PatchResult {
        val result = HideWatermarkParentSignature.result!!

        val method = result.findParentMethod(HideWatermarkSignature)?.method!!
        method.addInstructions(method.implementation!!.instructions.size-4, """
            invoke-static {p2}, Lapp.revanced.integrations.patches.BrandingWaterMarkPatch;->isBrandingWatermarkShown(Z)Z
            move-result p2
        """)

        return PatchResultSuccess()
    }
}
