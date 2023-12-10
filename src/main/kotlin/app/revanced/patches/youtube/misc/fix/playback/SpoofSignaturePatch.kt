package app.revanced.patches.youtube.misc.fix.playback

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.extensions.InstructionExtensions.replaceInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patcher.util.smali.ExternalLabel
import app.revanced.patches.youtube.misc.strings.StringsPatch
import app.revanced.patches.shared.settings.preference.impl.PreferenceScreen
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.youtube.misc.fix.playback.fingerprints.PlayerResponseModelImplGeneralFingerprint
import app.revanced.patches.youtube.misc.fix.playback.fingerprints.PlayerResponseModelImplLiveStreamFingerprint
import app.revanced.patches.youtube.misc.fix.playback.fingerprints.PlayerResponseModelImplRecommendedLevelFingerprint
import app.revanced.patches.youtube.misc.fix.playback.fingerprints.ScrubbedPreviewLayoutFingerprint
import app.revanced.patches.youtube.misc.fix.playback.fingerprints.StoryboardRendererDecoderRecommendedLevelFingerprint
import app.revanced.patches.youtube.misc.fix.playback.fingerprints.StoryboardRendererDecoderSpecFingerprint
import app.revanced.patches.youtube.misc.fix.playback.fingerprints.StoryboardRendererSpecFingerprint
import app.revanced.patches.youtube.misc.fix.playback.fingerprints.StoryboardThumbnailFingerprint
import app.revanced.patches.youtube.misc.fix.playback.fingerprints.StoryboardThumbnailParentFingerprint
import app.revanced.patches.youtube.misc.playertype.PlayerTypeHookPatch
import app.revanced.patches.youtube.misc.settings.SettingsPatch
import app.revanced.patches.youtube.video.information.VideoInformationPatch
import app.revanced.patches.youtube.video.playerresponse.PlayerResponseMethodHookPatch
import app.revanced.util.exception
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction

@Patch(
    description = "Spoofs the signature to prevent playback issues.",
    dependencies = [
        SettingsPatch::class,
        PlayerTypeHookPatch::class,
        PlayerResponseMethodHookPatch::class,
        VideoInformationPatch::class,
        SpoofSignatureResourcePatch::class
    ]
)
object SpoofSignaturePatch : BytecodePatch(
    setOf(
        PlayerResponseModelImplGeneralFingerprint,
        PlayerResponseModelImplLiveStreamFingerprint,
        PlayerResponseModelImplRecommendedLevelFingerprint,
        StoryboardRendererSpecFingerprint,
        StoryboardRendererDecoderSpecFingerprint,
        StoryboardRendererDecoderRecommendedLevelFingerprint,
        StoryboardThumbnailParentFingerprint,
        ScrubbedPreviewLayoutFingerprint,
    )
) {
    private const val INTEGRATIONS_CLASS_DESCRIPTOR =
        "Lapp/revanced/integrations/patches/spoof/SpoofSignaturePatch;"

    override fun execute(context: BytecodeContext) {
        StringsPatch.includePatchStrings("SpoofSignature")
        SettingsPatch.PreferenceScreen.MISC.addPreferences(
            PreferenceScreen(
                "revanced_spoof_signature_verification_screen",
                "revanced_spoof_signature_verification_screen_title",
                listOf(
                    SwitchPreference(
                        "revanced_spoof_signature_verification_enabled",
                        "revanced_spoof_signature_verification_enabled_title",
                        "revanced_spoof_signature_verification_enabled_summary_on",
                        "revanced_spoof_signature_verification_enabled_summary_off"
                    ),
                    SwitchPreference(
                        "revanced_spoof_signature_in_feed_enabled",
                        "revanced_spoof_signature_in_feed_enabled_title",
                        "revanced_spoof_signature_in_feed_enabled_summary_on",
                        "revanced_spoof_signature_in_feed_enabled_summary_off"
                    ),
                    SwitchPreference(
                        "revanced_spoof_storyboard",
                        "revanced_spoof_storyboard_title",
                        "revanced_spoof_storyboard_summary_on",
                        "revanced_spoof_storyboard_summary_off"
                    )
                ),
                "revanced_spoof_signature_verification_screen_summary"
            )
        )

        // Hook the player parameters.
        PlayerResponseMethodHookPatch += PlayerResponseMethodHookPatch.Hook.ProtoBufferParameter(
            "$INTEGRATIONS_CLASS_DESCRIPTOR->spoofParameter(Ljava/lang/String;Z)Ljava/lang/String;"
        )

        // Force the seekbar time and chapters to always show up.
        // This is used if the storyboard spec fetch fails, for viewing paid videos,
        // or if storyboard spoofing is turned off.
        StoryboardThumbnailParentFingerprint.result?.classDef?.let { classDef ->
            StoryboardThumbnailFingerprint.also {
                it.resolve(
                    context,
                    classDef
                )
            }.result?.let {
                val endIndex = it.scanResult.patternScanResult!!.endIndex
                // Replace existing instruction to preserve control flow label.
                // The replaced return instruction always returns false
                // (it is the 'no thumbnails found' control path),
                // so there is no need to pass the existing return value to integrations.
                it.mutableMethod.replaceInstruction(
                    endIndex,
                    """
                        invoke-static {}, $INTEGRATIONS_CLASS_DESCRIPTOR->getSeekbarThumbnailOverrideValue()Z
                    """
                )
                // Since this is end of the method must replace one line then add the rest.
                it.mutableMethod.addInstructions(
                    endIndex + 1,
                    """
                    move-result v0
                    return v0
                """
                )
            } ?: throw StoryboardThumbnailFingerprint.exception
        }

        // If storyboard spoofing is turned off, then hide the empty seekbar thumbnail view.
        ScrubbedPreviewLayoutFingerprint.result?.apply {
            val endIndex = scanResult.patternScanResult!!.endIndex
            mutableMethod.apply {
                val imageViewFieldName = getInstruction<ReferenceInstruction>(endIndex).reference
                addInstructions(
                    implementation!!.instructions.lastIndex,
                    """
                        iget-object v0, p0, $imageViewFieldName   # copy imageview field to a register
                        invoke-static {v0}, $INTEGRATIONS_CLASS_DESCRIPTOR->seekbarImageViewCreated(Landroid/widget/ImageView;)V
                    """
                )
            }
        } ?: throw ScrubbedPreviewLayoutFingerprint.exception

        /**
         * Hook StoryBoard renderer url
         */
        arrayOf(
            PlayerResponseModelImplGeneralFingerprint,
            PlayerResponseModelImplLiveStreamFingerprint
        ).forEach { fingerprint ->
            fingerprint.result?.let {
            it.mutableMethod.apply {
                val getStoryBoardIndex = it.scanResult.patternScanResult!!.endIndex
                val getStoryBoardRegister =
                    getInstruction<OneRegisterInstruction>(getStoryBoardIndex).registerA

                    addInstructions(
                        getStoryBoardIndex,
                        """
                        invoke-static { v$getStoryBoardRegister }, $INTEGRATIONS_CLASS_DESCRIPTOR->getStoryboardRendererSpec(Ljava/lang/String;)Ljava/lang/String;
                        move-result-object v$getStoryBoardRegister
                    """
                    )
                }
            } ?: throw fingerprint.exception
        }

        // Hook recommended seekbar thumbnails quality level.
        StoryboardRendererDecoderRecommendedLevelFingerprint.result?.let {
            val moveOriginalRecommendedValueIndex = it.scanResult.patternScanResult!!.endIndex
            val originalValueRegister = it.mutableMethod
                .getInstruction<OneRegisterInstruction>(moveOriginalRecommendedValueIndex).registerA

            it.mutableMethod.addInstructions(
                moveOriginalRecommendedValueIndex + 1, """
                        invoke-static { v$originalValueRegister }, $INTEGRATIONS_CLASS_DESCRIPTOR->getRecommendedLevel(I)I
                        move-result v$originalValueRegister
                """
            )
        } ?: throw StoryboardRendererDecoderRecommendedLevelFingerprint.exception

        // Hook the recommended precise seeking thumbnails quality level.
        PlayerResponseModelImplRecommendedLevelFingerprint.result?.let {
            it.mutableMethod.apply {
                val moveOriginalRecommendedValueIndex = it.scanResult.patternScanResult!!.endIndex
                val originalValueRegister =
                    getInstruction<OneRegisterInstruction>(moveOriginalRecommendedValueIndex).registerA

                addInstructions(
                    moveOriginalRecommendedValueIndex, """
                        invoke-static { v$originalValueRegister }, $INTEGRATIONS_CLASS_DESCRIPTOR->getRecommendedLevel(I)I
                        move-result v$originalValueRegister
                        """
                )
            }
        } ?: throw PlayerResponseModelImplRecommendedLevelFingerprint.exception

        StoryboardRendererSpecFingerprint.result?.let {
            it.mutableMethod.apply {
                val storyBoardUrlParams = 0

                addInstructionsWithLabels(
                    0,
                    """
                        if-nez p$storyBoardUrlParams, :ignore
                        invoke-static { p$storyBoardUrlParams }, $INTEGRATIONS_CLASS_DESCRIPTOR->getStoryboardRendererSpec(Ljava/lang/String;)Ljava/lang/String;
                        move-result-object p$storyBoardUrlParams
                    """,
                    ExternalLabel("ignore", getInstruction(0))
                )
            }
        } ?: throw StoryboardRendererSpecFingerprint.exception

        // Hook the seekbar thumbnail decoder and use a NULL spec for live streams.
        StoryboardRendererDecoderSpecFingerprint.result?.let {
            val storyBoardUrlIndex = it.scanResult.patternScanResult!!.startIndex + 1
            val storyboardUrlRegister =
                it.mutableMethod.getInstruction<OneRegisterInstruction>(storyBoardUrlIndex).registerA

            it.mutableMethod.addInstructions(
                storyBoardUrlIndex + 1, """
                        invoke-static { v$storyboardUrlRegister }, $INTEGRATIONS_CLASS_DESCRIPTOR->getStoryboardDecoderRendererSpec(Ljava/lang/String;)Ljava/lang/String;
                        move-result-object v$storyboardUrlRegister
                """
            )
        } ?: throw StoryboardRendererDecoderSpecFingerprint.exception
    }
}
