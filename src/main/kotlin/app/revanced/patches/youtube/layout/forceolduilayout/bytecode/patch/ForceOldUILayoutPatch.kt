package app.revanced.patches.youtube.layout.forceolduilayout.bytecode.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.shared.settings.preference.impl.StringResource
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.youtube.layout.forceolduilayout.bytecode.fingerprints.ForceOldUILayoutFingerprint
import app.revanced.patches.youtube.layout.homepage.breakingnews.annotations.ForceOldUILayoutCompatibility
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction

@Patch
@DependsOn([IntegrationsPatch::class, SettingsPatch::class])
@Name("force-old-ui-layout")
@Description("Spoof the YouTube client version to force the old UI layout (experimental).")
@ForceOldUILayoutCompatibility
@Version("0.0.1")
class ForceOldUILayoutPatch : BytecodePatch(
    listOf(
        ForceOldUILayoutFingerprint
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {
        val result = ForceOldUILayoutFingerprint.result!!
        val method = result.mutableMethod
        val index = result.scanResult.patternScanResult!!.startIndex
        val register = (method.implementation!!.instructions[index] as OneRegisterInstruction).registerA

        method.addInstructions(
            index + 1, """
            invoke-static {v$register}, Lapp/revanced/integrations/patches/ForceOldUILayoutPatch;->getYouTubeVersionOverride(Ljava/lang/String;)Ljava/lang/String;
            move-result-object v$register
        """
        )

        SettingsPatch.PreferenceScreen.LAYOUT.addPreferences(
            SwitchPreference(
                "revanced_force_old_ui_layout",
                StringResource("revanced_force_old_ui_layout_title", "Force old UI layout (experimental)"),
                false,
                StringResource("revanced_force_old_ui_layout_summary_on", "Old UI layout forced"),
                StringResource("revanced_force_old_ui_layout_summary_off", "Old UI layout not forced")
            )
        )

        return PatchResultSuccess()
    }
}