package app.revanced.patches.youtube.layout.forceolduilayout.bytecode.patch

import app.revanced.extensions.toErrorResult
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
import app.revanced.patches.youtube.layout.forceolduilayout.annotations.ForceOldUILayoutCompatibility
import app.revanced.patches.youtube.layout.forceolduilayout.bytecode.fingerprints.OverrideBuildVersionFingerprint
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
        OverrideBuildVersionFingerprint
    )
) {
    companion object {
        const val INTEGRATIONS_CLASS_DESCRIPTOR = "Lapp/revanced/integrations/patches/ForceOldUILayoutPatch;"
    }

    override fun execute(context: BytecodeContext): PatchResult {
        SettingsPatch.PreferenceScreen.LAYOUT.addPreferences(
            SwitchPreference(
                "revanced_force_old_ui_layout",
                StringResource("revanced_force_old_ui_layout_title", "Force old UI layout (experimental)"),
                false,
                StringResource("revanced_force_old_ui_layout_summary_on", "Old UI layout forced"),
                StringResource("revanced_force_old_ui_layout_summary_off", "Old UI layout not forced")
            )
        )

        OverrideBuildVersionFingerprint.result?.apply {
            val insertIndex = scanResult.patternScanResult!!.startIndex + 1
            val buildOverrideNameRegister =
                (mutableMethod.implementation!!.instructions[insertIndex - 1] as OneRegisterInstruction).registerA

            mutableMethod.addInstructions(
                insertIndex,
                """
                        invoke-static {v$buildOverrideNameRegister}, $INTEGRATIONS_CLASS_DESCRIPTOR;->getYouTubeVersionOverride(Ljava/lang/String;)Ljava/lang/String;
                        move-result-object v$buildOverrideNameRegister
                         """
            )
        } ?: return OverrideBuildVersionFingerprint.toErrorResult()

        return PatchResultSuccess()
    }
}