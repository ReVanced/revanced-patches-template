package app.revanced.patches.youtube.video.videoqualitymenu.patch

import app.revanced.extensions.toErrorResult
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
import app.revanced.patches.youtube.misc.litho.filter.patch.LithoFilterPatch
import app.revanced.patches.youtube.video.videoqualitymenu.annotations.OldVideoQualityMenuCompatibility
import app.revanced.patches.youtube.video.videoqualitymenu.fingerprints.CreateBottomSheetFingerprint
import app.revanced.patches.youtube.video.videoqualitymenu.fingerprints.VideoQualityMenuViewInflateFingerprint
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction

@Patch
@DependsOn([IntegrationsPatch::class, OldVideoQualityMenuResourcePatch::class, LithoFilterPatch::class])
@Name("old-video-quality-menu")
@Description("Shows the old video quality with the advanced video quality options instead of the new one.")
@OldVideoQualityMenuCompatibility
@Version("0.0.1")
class OldVideoQualityMenuPatch : BytecodePatch(
    listOf(VideoQualityMenuViewInflateFingerprint, CreateBottomSheetFingerprint)
) {
    override fun execute(context: BytecodeContext): PatchResult {
        // region Patch for the old type of the video quality menu.

        VideoQualityMenuViewInflateFingerprint.result?.let {
            it.mutableMethod.apply {
                val checkCastIndex = it.scanResult.patternScanResult!!.endIndex
                val listViewRegister = getInstruction<OneRegisterInstruction>(checkCastIndex).registerA

                addInstruction(
                    checkCastIndex + 1,
                    "invoke-static { v$listViewRegister }, " +
                            "$INTEGRATIONS_CLASS_DESCRIPTOR->" +
                            "showOldVideoQualityMenu(Landroid/widget/ListView;)V"
                )
            }
        }

        // endregion

        // region Patch for the new type of the video quality menu.

        CreateBottomSheetFingerprint.result?.let {
            it.mutableMethod.apply {
                val insertIndex = implementation!!.instructions.size - 1
                val insertRegister = getInstruction<OneRegisterInstruction>(insertIndex).registerA

                addInstruction(
                    insertIndex,
                    "invoke-static { v$insertRegister }, $INTEGRATIONS_CLASS_DESCRIPTOR->" +
                            "showOldVideoQualityMenu(Landroid/widget/LinearLayout;)V"
                )
            }
        } ?: return CreateBottomSheetFingerprint.toErrorResult()


        // Required to check if the video quality menu is currently shown in order to click on the "Advanced" item.
        LithoFilterPatch.addFilter(FILTER_CLASS_DESCRIPTOR)

        // endregion

        return PatchResultSuccess()
    }

    private companion object {
        private const val FILTER_CLASS_DESCRIPTOR =
            "Lapp/revanced/integrations/patches/components/VideoQualityMenuFilterPatch;"

        private const val INTEGRATIONS_CLASS_DESCRIPTOR =
            "Lapp/revanced/integrations/patches/playback/quality/OldVideoQualityMenuPatch;"
    }
}