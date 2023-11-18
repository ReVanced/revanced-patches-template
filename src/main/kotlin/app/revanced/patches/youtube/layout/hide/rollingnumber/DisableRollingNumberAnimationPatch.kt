package app.revanced.patches.youtube.layout.hide.rollingnumber

import app.revanced.extensions.exception
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patcher.util.smali.ExternalLabel
import app.revanced.patches.shared.settings.preference.impl.StringResource
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.youtube.layout.hide.rollingnumber.fingerprints.RollingNumberTextViewAnimationUpdateFingerprint
import app.revanced.patches.youtube.misc.integrations.IntegrationsPatch
import app.revanced.patches.youtube.misc.settings.SettingsPatch
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction

@Patch(
    name = "Disable rolling number animations",
    description = "Disables rolling number animations of video view count, upload time, and user likes",
    dependencies = [IntegrationsPatch::class, SettingsPatch::class],
    compatiblePackages = [
        CompatiblePackage(
            "com.google.android.youtube", [
                "18.43.45",
                "18.44.41",
                "18.45.41"
            ]
        )
    ]
)
@Suppress("unused")
object DisableRollingNumberAnimationPatch : BytecodePatch(
    setOf(
        RollingNumberTextViewAnimationUpdateFingerprint
    )
) {
    private const val INTEGRATIONS_CLASS_DESCRIPTOR =
        "Lapp/revanced/integrations/patches/DisableRollingNumberAnimationsPatch;"

    override fun execute(context: BytecodeContext) {
        SettingsPatch.PreferenceScreen.LAYOUT.addPreferences(
            SwitchPreference(
                "revanced_disable_rolling_number_animations",
                StringResource("revanced_disable_rolling_number_animations_title", "Disable rolling number animations"),
                StringResource("revanced_disable_rolling_number_animations_summary_on", "Rolling numbers are not animated"),
                StringResource("revanced_disable_rolling_number_animations_summary_off", "Rolling numbers are animated")
            )
        )

        // Animations are disabled by preventing an Image from being applied to the text span,
        // which prevents the animations from appearing.
        RollingNumberTextViewAnimationUpdateFingerprint.result?.apply {
            val patternScanResult = scanResult.patternScanResult!!
            val blockStartIndex = patternScanResult.startIndex
            val blockEndIndex = patternScanResult.endIndex + 1
            mutableMethod.apply {
                val freeRegister = getInstruction<OneRegisterInstruction>(blockStartIndex).registerA

                // ReturnYouTubeDislike also makes changes to this same method,
                // and must add control flow label to a noop instruction to
                // ensure RYD patch adds it's changes after the control flow label.
                addInstructions(blockEndIndex, "nop")

                addInstructionsWithLabels(
                    blockStartIndex,
                    """
                        invoke-static { }, $INTEGRATIONS_CLASS_DESCRIPTOR->disableRollingNumberAnimations()Z
                        move-result v$freeRegister
                        if-nez v$freeRegister, :disable_animations
                    """,
                    ExternalLabel("disable_animations", getInstruction(blockEndIndex))
                )
            }
        } ?: throw RollingNumberTextViewAnimationUpdateFingerprint.exception
    }
}
