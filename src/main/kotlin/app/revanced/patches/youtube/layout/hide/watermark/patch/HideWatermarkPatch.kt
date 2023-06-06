package app.revanced.patches.youtube.layout.hide.watermark.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.extensions.removeInstruction
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint.Companion.resolve
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultError
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.youtube.layout.hide.watermark.annotations.HideWatermarkCompatibility
import app.revanced.patches.youtube.layout.hide.watermark.fingerprints.HideWatermarkFingerprint
import app.revanced.patches.youtube.layout.hide.watermark.fingerprints.HideWatermarkParentFingerprint
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
import app.revanced.patches.youtube.misc.settings.bytecode.patch.YouTubeSettingsPatch

@Patch
@DependsOn([IntegrationsPatch::class, YouTubeSettingsPatch::class])
@Name("hide-watermark")
@Description("Hides creator's watermarks on videos.")
@HideWatermarkCompatibility
@Version("0.0.1")
class HideWatermarkPatch : BytecodePatch(
    listOf(
        HideWatermarkParentFingerprint
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {
        YouTubeSettingsPatch.PreferenceScreen.LAYOUT.addPreferences(
            SwitchPreference(
                "revanced_hide_video_watermark",
                "revanced_hide_video_watermark_title",
                "revanced_hide_video_watermark_summary_on",
                "revanced_hide_video_watermark_summary_off"
            )
        )

        HideWatermarkFingerprint.resolve(context, HideWatermarkParentFingerprint.result!!.classDef)
        val result = HideWatermarkFingerprint.result
            ?: return PatchResultError("Required parent method could not be found.")

        val method = result.mutableMethod
        val line = method.implementation!!.instructions.size - 5

        method.removeInstruction(line)
        method.addInstructions(
            line, """
            invoke-static {}, Lapp/revanced/integrations/patches/BrandingWaterMarkPatch;->isBrandingWatermarkShown()Z
            move-result p2
        """
        )

        return PatchResultSuccess()
    }
}
