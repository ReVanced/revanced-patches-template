package app.revanced.patches.youtube.layout.hide.floatingmicrophone.patch

import app.revanced.extensions.error
import app.revanced.patcher.BytecodeContext
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.youtube.layout.hide.floatingmicrophone.annotations.HideFloatingMicrophoneButtonCompatibility
import app.revanced.patches.youtube.layout.hide.floatingmicrophone.fingerprints.ShowFloatingMicrophoneButtonFingerprint
import org.jf.dexlib2.iface.instruction.TwoRegisterInstruction

@Patch
@Name("Hide floating microphone button")
@Description("Hides the floating microphone button which appears in search.")
@DependsOn([HideFloatingMicrophoneButtonResourcePatch::class])
@HideFloatingMicrophoneButtonCompatibility
@Version("0.0.1")
class HideFloatingMicrophoneButtonPatch : BytecodePatch(
    listOf(ShowFloatingMicrophoneButtonFingerprint)
) {
    override suspend fun execute(context: BytecodeContext) {
        ShowFloatingMicrophoneButtonFingerprint.result?.let { result ->
            with(result.mutableMethod) {
                val insertIndex = result.scanResult.patternScanResult!!.startIndex + 1
                val showButtonRegister = getInstruction<TwoRegisterInstruction>(insertIndex - 1).registerA

                addInstructions(
                    insertIndex,
                    """
                        invoke-static {v$showButtonRegister}, $INTEGRATIONS_CLASS_DESCRIPTOR->hideFloatingMicrophoneButton(Z)Z
                        move-result v$showButtonRegister
                        """
                )
            }
        } ?: ShowFloatingMicrophoneButtonFingerprint.error()

    }

    private companion object {
        const val INTEGRATIONS_CLASS_DESCRIPTOR =
            "Lapp/revanced/integrations/patches/HideFloatingMicrophoneButtonPatch;"
    }
}
