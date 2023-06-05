package app.revanced.patches.youtube.layout.hide.seekbar.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint.Companion.resolve
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.shared.fingerprints.SeekbarFingerprint
import app.revanced.patches.shared.fingerprints.SeekbarOnDrawFingerprint
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.youtube.layout.hide.seekbar.annotations.HideSeekbarCompatibility
import app.revanced.patches.youtube.layout.seekbar.bytecode.patch.SeekbarColorBytecodePatch
import app.revanced.patches.youtube.layout.seekbar.resource.SeekbarPreferencesPatch
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch

@Patch
@DependsOn([
    IntegrationsPatch::class,
    SettingsPatch::class,
    // Does not alter the behavior of the seek bar by default.
    SeekbarColorBytecodePatch::class,
    // Used to add preferences to the seekbar settings menu.
    SeekbarPreferencesPatch::class
])
@Name("hide-seekbar")
@Description("Hides the seekbar.")
@HideSeekbarCompatibility
@Version("0.0.1")
class HideSeekbarPatch : BytecodePatch(
    listOf(SeekbarFingerprint)
) {
    override fun execute(context: BytecodeContext): PatchResult {
        SeekbarPreferencesPatch.addPreferences(
            SwitchPreference(
                "revanced_hide_seekbar",
                "revanced_hide_seekbar_title",
                "revanced_hide_seekbar_summary_on",
                "revanced_hide_seekbar_summary_off"
            ),
            SwitchPreference(
                "revanced_hide_seekbar_thumbnail",
                "revanced_hide_seekbar_thumbnail_title",
                "revanced_hide_seekbar_thumbnail_summary_on",
                "revanced_hide_seekbar_thumbnail_summary_off"
            )
        )

        SeekbarFingerprint.result!!.let {
            SeekbarOnDrawFingerprint.apply { resolve(context, it.mutableClass) }
        }.result!!.mutableMethod.addInstructions(
            0, """
            const/4 v0, 0x0
            invoke-static { }, Lapp/revanced/integrations/patches/HideSeekbarPatch;->hideSeekbar()Z
            move-result v0
            if-eqz v0, :hide_seekbar
            return-void
            :hide_seekbar
            nop
        """
        )

        return PatchResultSuccess()
    }
}
