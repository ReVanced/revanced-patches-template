package app.revanced.patches.youtube.misc.videobuffer.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.impl.BytecodeData
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.patch.impl.BytecodePatch
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch
import app.revanced.patches.youtube.misc.settings.framework.components.impl.InputType
import app.revanced.patches.youtube.misc.settings.framework.components.impl.PreferenceScreen
import app.revanced.patches.youtube.misc.settings.framework.components.impl.StringResource
import app.revanced.patches.youtube.misc.settings.framework.components.impl.TextPreference
import app.revanced.patches.youtube.misc.videobuffer.annotations.CustomVideoBufferCompatibility
import app.revanced.patches.youtube.misc.videobuffer.fingerprints.MaxBufferFingerprint
import app.revanced.patches.youtube.misc.videobuffer.fingerprints.PlaybackBufferFingerprint
import app.revanced.patches.youtube.misc.videobuffer.fingerprints.ReBufferFingerprint
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction

@Patch
@Name("custom-video-buffer")
@Description("Lets you change the buffers of videos.")
@DependsOn([SettingsPatch::class])
@CustomVideoBufferCompatibility
@Version("0.0.1")
class CustomVideoBufferPatch : BytecodePatch(
    listOf(
        MaxBufferFingerprint, PlaybackBufferFingerprint, ReBufferFingerprint
    )
) {
    override fun execute(data: BytecodeData): PatchResult {
        SettingsPatch.PreferenceScreen.MISC.addPreferences(
            PreferenceScreen(
                "revanced_custom_video_buffer",
                StringResource("revanced_custom_video_buffer_title", "Video buffer settings"),
                listOf(
                    TextPreference(
                        "revanced_pref_max_buffer_ms",
                        StringResource("revanced_pref_max_buffer_ms_title", "Maximum buffer size"),
                        InputType.NUMBER,
                        "120000",
                        StringResource(
                            "revanced_pref_max_buffer_ms_summary",
                            "The maximum size of buffer"
                        )
                    ),
                    TextPreference(
                        "revanced_pref_buffer_for_playback_ms",
                        StringResource("revanced_pref_buffer_for_playback_ms_title", "Maximum buffer for playback"),
                        InputType.NUMBER,
                        "2500",
                        StringResource(
                            "revanced_pref_buffer_for_playback_ms_summary",
                            "Maximum size of a buffer for playback in milliseconds."
                        )
                    ),
                    TextPreference(
                        "revanced_pref_buffer_for_playback_after_rebuffer_ms",
                        StringResource(
                            "revanced_pref_buffer_for_playback_after_rebuffer_ms_title",
                            "Maximum buffer for playback after rebuffer in milliseconds"
                        ),
                        InputType.NUMBER,
                        "5000",
                        StringResource(
                            "revanced_pref_buffer_for_playback_after_rebuffer_ms_summary",
                            "Maximum size of a buffer for playback after rebuffering in milliseconds."
                        )
                    )
                ),
                StringResource("revanced_custom_video_buffer_summary", "Custom settings for video buffer")
            )
        )

        execMaxBuffer(data)
        execPlaybackBuffer(data)
        execReBuffer(data)
        return PatchResultSuccess()
    }

    private fun execMaxBuffer(data: BytecodeData) {
        val result = MaxBufferFingerprint.result!!
        val method = result.mutableMethod
        val index = result.patternScanResult!!.endIndex - 1
        val register = (method.implementation!!.instructions.get(index) as OneRegisterInstruction).registerA
        method.addInstructions(
            index + 1, """
           invoke-static {}, Lapp/revanced/integrations/patches/VideoBufferPatch;->getMaxBuffer()I
           move-result v$register
        """
        )
    }

    private fun execPlaybackBuffer(data: BytecodeData) {
        val result = PlaybackBufferFingerprint.result!!
        val method = result.mutableMethod
        val index = result.patternScanResult!!.startIndex
        val register = (method.implementation!!.instructions.get(index) as OneRegisterInstruction).registerA
        method.addInstructions(
            index + 1, """
           invoke-static {}, Lapp/revanced/integrations/patches/VideoBufferPatch;->getPlaybackBuffer()I
           move-result v$register
        """
        )
    }

    private fun execReBuffer(data: BytecodeData) {
        val result = ReBufferFingerprint.result!!
        val method = result.mutableMethod
        val index = result.patternScanResult!!.startIndex
        val register = (method.implementation!!.instructions.get(index) as OneRegisterInstruction).registerA
        method.addInstructions(
            index + 1, """
           invoke-static {}, Lapp/revanced/integrations/patches/VideoBufferPatch;->getReBuffer()I
           move-result v$register
        """
        )
    }
}
