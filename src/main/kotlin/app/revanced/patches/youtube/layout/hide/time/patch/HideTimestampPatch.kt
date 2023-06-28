package app.revanced.patches.youtube.layout.hide.time.patch

import app.revanced.extensions.toErrorResult
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.youtube.layout.hide.time.annotations.HideTimeCompatibility
import app.revanced.patches.youtube.layout.hide.time.fingerprints.TimeCounterFingerprint
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
import app.revanced.patches.youtube.misc.settings.bytecode.patch.YouTubeSettingsPatch

@Patch
@DependsOn([IntegrationsPatch::class, YouTubeSettingsPatch::class])
@Name("hide-timestamp")
@Description("Hides timestamp in video player.")
@HideTimeCompatibility
@Version("0.0.1")
class HideTimestampPatch : BytecodePatch(
    listOf(
        TimeCounterFingerprint
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {
        YouTubeSettingsPatch.PreferenceScreen.LAYOUT.addPreferences(
            SwitchPreference(
                "revanced_hide_timestamp",
                "revanced_hide_timestamp_title",
                "revanced_hide_timestamp_summary_on",
                "revanced_hide_timestamp_summary_off"
            )
        )

        TimeCounterFingerprint.result?.apply {
            mutableMethod.addInstructionsWithLabels(
            0,
            """
                invoke-static { }, Lapp/revanced/integrations/patches/HideTimestampPatch;->hideTimestamp()Z
                move-result v0
                if-eqz v0, :hide_time
                return-void
                :hide_time
                nop
            """
            )
        } ?: return TimeCounterFingerprint.toErrorResult()

        return PatchResultSuccess()
    }
}
