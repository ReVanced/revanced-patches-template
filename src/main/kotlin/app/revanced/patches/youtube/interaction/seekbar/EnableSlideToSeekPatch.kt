package app.revanced.patches.youtube.interaction.seekbar

import app.revanced.extensions.exception
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patches.shared.settings.preference.impl.StringResource
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.youtube.interaction.seekbar.fingerprints.DoubleSpeedSeekNoticeFingerprint
import app.revanced.patches.youtube.interaction.seekbar.fingerprints.SlideToSeekFingerprint
import app.revanced.patches.youtube.misc.integrations.IntegrationsPatch
import app.revanced.patches.youtube.misc.settings.SettingsPatch
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction

@Patch(
    name = "Enable slide to seek",
    description = "Enable slide to seek instead of playing at 2x speed.",
    dependencies = [IntegrationsPatch::class, SettingsPatch::class],
    compatiblePackages = [
        CompatiblePackage(
            "com.google.android.youtube",
            [
                "18.43.45",
                "18.44.41",
                "18.45.41"
            ]
        )
    ]
)
@Suppress("unused")
object EnableSlideToSeekPatch : BytecodePatch(
    setOf(
        SlideToSeekFingerprint,
        DoubleSpeedSeekNoticeFingerprint
    )
) {
    const val INTEGRATIONS_CLASS_DESCRIPTOR = "Lapp/revanced/integrations/patches/SlideToSeekPatch;"

    override fun execute(context: BytecodeContext) {
        SettingsPatch.PreferenceScreen.INTERACTIONS.addPreferences(
            SwitchPreference(
                "revanced_slide_to_seek",
                StringResource(
                    "revanced_slide_to_seek_title",
                    "Enable slide to seek"
                ),
                StringResource(
                    "revanced_slide_to_seek_summary_on",
                    "Slide to seek is enabled"
                ),
                StringResource(
                    "revanced_slide_to_seek_summary_off",
                    "Slide to seek is not enabled"
                ),
            )
        )

        arrayOf(
            // Restore the behaviour to slide to seek.
            SlideToSeekFingerprint,
            // Disable the double speed seek notice.
            DoubleSpeedSeekNoticeFingerprint
        ).map {
            it.result ?: throw it.exception
        }.forEach {
            val insertIndex = it.scanResult.patternScanResult!!.endIndex + 1

            it.mutableMethod.apply {
                val isEnabledRegister = getInstruction<OneRegisterInstruction>(insertIndex).registerA

                addInstructions(
                    insertIndex,
                    """
                        invoke-static { }, $INTEGRATIONS_CLASS_DESCRIPTOR->isSlideToSeekDisabled()Z
                        move-result v$isEnabledRegister
                    """
                )
            }
        }
    }
}