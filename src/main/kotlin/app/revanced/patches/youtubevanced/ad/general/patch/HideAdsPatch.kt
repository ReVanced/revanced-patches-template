package app.revanced.patches.youtubevanced.ad.general.patch

import app.revanced.extensions.toErrorResult
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.extensions.instruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.shared.misc.fix.verticalscroll.patch.VerticalScrollPatch
import app.revanced.patches.youtubevanced.ad.general.annotations.HideAdsCompatibility
import app.revanced.patches.youtubevanced.ad.general.fingerprints.ContainsAdFingerprint
import org.jf.dexlib2.iface.instruction.formats.Instruction21c

@Patch
@Name("hide-ads")
@Description("Removes ads from YouTube Vanced.")
@DependsOn([VerticalScrollPatch::class])
@HideAdsCompatibility
@Version("0.0.1")
class HideAdsPatch : BytecodePatch(
    listOf(
        ContainsAdFingerprint
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {
        val result = ContainsAdFingerprint.result ?: return ContainsAdFingerprint.toErrorResult()
        val insertIndex = result.scanResult.patternScanResult!!.endIndex + 1
        with(result.mutableMethod){
            val register = (instruction(insertIndex - 2) as Instruction21c).registerA
            listOf(
                "video_display_full_layout", "active_view_display_container", "|ad_", "|ads_",
                "ads_video_with_context", "legal_disclosure_cell", "primetime_promo",
                "brand_video_shelf", "hero_promo_image", "statement_banner",
                "square_image_layout", "watch_metadata_app_promo", "_ad_with",
                "landscape_image_wide_button_layout", "cell_divider", "carousel_ad",
                "video_display_full_buttoned_layout"
            ).forEach { component ->
                this.addInstructions(
                    insertIndex,
                    """
                           const-string v$register, "$component"
                           invoke-interface {v0, v$register}, Ljava/util/List;->add(Ljava/lang/Object;)Z
                        """
                )
            }
        }
        listOf(
            ContainsAdFingerprint
        ).map { it.result ?: return it.toErrorResult() }.forEach{

        }

        return PatchResultSuccess()
    }
}