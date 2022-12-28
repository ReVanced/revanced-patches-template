package app.revanced.patches.youtube.misc.fix.playback.patch

import app.revanced.extensions.toErrorResult
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.extensions.removeInstruction
import app.revanced.patcher.extensions.instruction
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint.Companion.resolve
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.util.smali.ExternalLabel
import app.revanced.patches.shared.settings.preference.impl.StringResource
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.youtube.misc.fix.playback.annotations.FixPlaybackCompatibility
import app.revanced.patches.youtube.misc.fix.playback.fingerprints.VideoEndListenerFingerprint
import app.revanced.patches.youtube.misc.fix.playback.fingerprints.VideoEndListenerLabelFingerprint
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch
import app.revanced.patches.youtube.misc.video.information.patch.VideoInformationPatch
import app.revanced.patches.youtube.misc.video.videoid.patch.VideoIdPatch
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction

@DependsOn([
    IntegrationsPatch::class,
    VideoInformationPatch::class, // updates video length and adds method to seek in video, necessary for this patch
    SettingsPatch::class,
    VideoIdPatch::class
])
@Name("fix-playback")
@Description("Fixes the issue with videos not playing when video ads are removed.")
@FixPlaybackCompatibility
@Version("0.0.1")
class FixPlaybackPatch : BytecodePatch(
    listOf(VideoEndListenerFingerprint, VideoEndListenerLabelFingerprint)
) {
    override fun execute(context: BytecodeContext): PatchResult {
        SettingsPatch.PreferenceScreen.MISC.addPreferences(
            SwitchPreference(
                "revanced_fix_playback",
                StringResource("revanced_fix_playback_title", "Fix video playback issues"),
                false,
                StringResource(
                    "revanced_fix_playback_summary_on",
                    "The fix is enabled"
                ),
                StringResource(
                    "revanced_fix_playback_summary_off",
                    "The fix is disabled"
                )
            )
        )

        // TODO: Improve code to be more ideal
        VideoEndListenerFingerprint.result?.let { result ->
            val insertIndex = result.scanResult.patternScanResult!!.startIndex
            val registerIndex = insertIndex - 1
            val jumpIndex = insertIndex + 2

            // TODO: Add 'fixPlaybackEnabled()Z' function to return value of SettingsEnum in integrations
            with(result.mutableMethod) {
                val instructions = implementation!!.instructions
                val register = (instructions[registerIndex] as OneRegisterInstruction).registerA
                addInstructions(
                    insertIndex, """
                        invoke-static {}, $INTEGRATIONS_CLASS_DESCRIPTOR->fixPlaybackEnabled()Z
                        move-result v$register
                        if-nez v$register, :stop_repeat
                    """, listOf(ExternalLabel("stop_repeat", instruction(jumpIndex)))
                )
            }

            VideoEndListenerLabelFingerprint.also { it.resolve(context, result.classDef) }.result?.let { labelResult ->
                val labelInsertIndex = labelResult.scanResult.patternScanResult!!.endIndex

                with(labelResult.mutableMethod) {
                    removeInstruction(labelInsertIndex)
                    addInstructions(
                        labelInsertIndex, """
                            goto :goto_new
                        """, listOf(ExternalLabel("goto_new", instruction(registerIndex)))
                    )
                }
            } ?: return VideoEndListenerLabelFingerprint.toErrorResult()
        } ?: return VideoEndListenerFingerprint.toErrorResult()

        // If a new video loads, fix the playback issue
        VideoIdPatch.injectCall("$INTEGRATIONS_CLASS_DESCRIPTOR->newVideoLoaded(Ljava/lang/String;)V")

        return PatchResultSuccess()
    }

    private companion object {
        const val INTEGRATIONS_CLASS_DESCRIPTOR =
            "Lapp/revanced/integrations/patches/FixPlaybackPatch;"
    }
}
