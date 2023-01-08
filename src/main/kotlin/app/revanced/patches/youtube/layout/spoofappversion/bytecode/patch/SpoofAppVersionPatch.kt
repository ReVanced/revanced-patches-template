package app.revanced.patches.youtube.layout.spoofappversion.bytecode.patch

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
import app.revanced.patches.youtube.layout.spoofappversion.annotations.SpoofAppVersionCompatibility
import app.revanced.patches.youtube.layout.spoofappversion.bytecode.fingerprints.SpoofAppVersionFingerprint
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction

@Patch
@DependsOn([IntegrationsPatch::class, SettingsPatch::class])
@Name("spoof-old-app-version")
@Description("Experimental patch that makes YouTube.com think the Android app is an older version. This brings back the old UI layout, but may also cause unknown effects.")
@SpoofAppVersionCompatibility
@Version("0.0.1")
class SpoofAppVersionPatch : BytecodePatch(
    listOf(
        SpoofAppVersionFingerprint
    )
) {
    companion object {
        const val INTEGRATIONS_CLASS_DESCRIPTOR = "Lapp/revanced/integrations/patches/SpoofAppVersionPatch"
    }

    override fun execute(context: BytecodeContext): PatchResult {
        SettingsPatch.PreferenceScreen.LAYOUT.addPreferences(
            SwitchPreference(
                "revanced_spoof_youtube_version",
                StringResource("revanced_spoof_youtube_version_title", "Experimental spoof of app version (show old UI layout)"),
                false,
                StringResource("revanced_spoof_youtube_version_summary_on", "Version spoofed to 17.30.34. If this is switched off, the old UI layout may persist until user logs out or the app data is cleared"),
                StringResource("revanced_spoof_youtube_version_summary_off", "Version not spoofed")
            )
        )

        SpoofAppVersionFingerprint.result?.apply {
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
        } ?: return SpoofAppVersionFingerprint.toErrorResult()

        return PatchResultSuccess()
    }
}