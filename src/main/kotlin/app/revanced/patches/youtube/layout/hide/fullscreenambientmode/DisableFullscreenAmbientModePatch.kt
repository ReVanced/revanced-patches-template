package app.revanced.patches.youtube.layout.hide.fullscreenambientmode

import app.revanced.extensions.exception
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.youtube.layout.hide.fullscreenambientmode.fingerprints.InitializeAmbientModeFingerprint
import app.revanced.patches.youtube.misc.integrations.IntegrationsPatch
import app.revanced.patches.youtube.misc.strings.StringsPatch
import app.revanced.patches.youtube.misc.settings.SettingsPatch

@Patch(
    name = "Disable fullscreen ambient mode",
    description = "Disables the ambient mode when in fullscreen.",
    dependencies = [SettingsPatch::class, IntegrationsPatch::class],
    compatiblePackages = [
        CompatiblePackage(
            "com.google.android.youtube", [
                "18.37.36",
                "18.38.44",
                "18.43.45",
                "18.44.41",
            ]
        )
    ]
)
@Suppress("unused")
object DisableFullscreenAmbientModePatch : BytecodePatch(
    setOf(InitializeAmbientModeFingerprint)
) {
    private const val INTEGRATIONS_CLASS_DESCRIPTOR =
        "Lapp/revanced/integrations/patches/DisableFullscreenAmbientModePatch;"

    override fun execute(context: BytecodeContext) {
        StringsPatch.includePatchStrings("DisableFullscreenAmbientMode")
        SettingsPatch.PreferenceScreen.LAYOUT.addPreferences(
            SwitchPreference(
                "revanced_disable_fullscreen_ambient_mode",
                "revanced_disable_fullscreen_ambient_mode_title",
                "revanced_disable_fullscreen_ambient_mode_summary_on",
                "revanced_disable_fullscreen_ambient_mode_summary_off",
            )
        )

        InitializeAmbientModeFingerprint.result?.let {
            it.mutableMethod.apply {
                val moveIsEnabledIndex = it.scanResult.patternScanResult!!.endIndex

                addInstruction(
                    moveIsEnabledIndex,
                    "invoke-static { }, " +
                            "$INTEGRATIONS_CLASS_DESCRIPTOR->enableFullScreenAmbientMode()Z"
                )
            }
        } ?: throw InitializeAmbientModeFingerprint.exception
    }
}
