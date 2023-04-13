package app.revanced.patches.youtube.misc.fix.playback.patch

import app.revanced.extensions.toErrorResult
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.data.toMethodWalker
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.extensions.instruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import app.revanced.patches.shared.settings.preference.impl.StringResource
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.youtube.misc.fix.playback.annotation.ProtobufSpoofCompatibility
import app.revanced.patches.youtube.misc.fix.playback.fingerprints.OpenCronetDataSourceFingerprint
import app.revanced.patches.youtube.misc.fix.playback.fingerprints.ProtobufParameterBuilderFingerprint
import app.revanced.patches.youtube.misc.fix.playback.fingerprints.SubtitleWindowSettingsConstructorFingerprint
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
import app.revanced.patches.youtube.misc.playertype.patch.PlayerTypeHookPatch
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction

@Patch
@Name("spoof-signature-verification")
@Description("Spoofs the client to prevent playback issues.")
@ProtobufSpoofCompatibility
@DependsOn([IntegrationsPatch::class, SettingsPatch::class, PlayerTypeHookPatch::class])
@Version("0.0.1")
class SpoofSignatureVerificationPatch : BytecodePatch(
    listOf(
        ProtobufParameterBuilderFingerprint,
        OpenCronetDataSourceFingerprint,
        SubtitleWindowSettingsConstructorFingerprint,
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {
        SettingsPatch.PreferenceScreen.MISC.addPreferences(
            SwitchPreference(
                "revanced_spoof_signature_verification",
                StringResource("revanced_spoof_signature_verification_title", "Spoof app signature"),
                true,
                StringResource("revanced_spoof_signature_verification_summary_on", "App signature spoofed"),
                StringResource("revanced_spoof_signature_verification_summary_off", "App signature not spoofed")
            )
        )

        // hook parameter
        ProtobufParameterBuilderFingerprint.result?.let {
            val setParamMethod = context
                .toMethodWalker(it.method)
                .nextMethod(it.scanResult.patternScanResult!!.startIndex, true).getMethod() as MutableMethod

            setParamMethod.apply {
                val protobufParameterRegister = 3

                addInstructions(
                    0,
                    """
                        invoke-static {p$protobufParameterRegister}, $INTEGRATIONS_CLASS_DESCRIPTOR->overrideProtobufParameter(Ljava/lang/String;)Ljava/lang/String;
                        move-result-object p$protobufParameterRegister
                    """
                )
            }
        } ?: return ProtobufParameterBuilderFingerprint.toErrorResult()

        // hook video playback result
        OpenCronetDataSourceFingerprint.result?.let {
            it.mutableMethod.apply {
                val getHeadersInstructionIndex = it.scanResult.patternScanResult!!.endIndex
                val responseCodeRegister =
                    (instruction(getHeadersInstructionIndex - 2) as OneRegisterInstruction).registerA

                addInstructions(
                    getHeadersInstructionIndex + 1,
                    """
                        invoke-static {v$responseCodeRegister}, $INTEGRATIONS_CLASS_DESCRIPTOR->onResponse(I)V
                    """
                )
            }

        } ?: return OpenCronetDataSourceFingerprint.toErrorResult()

        // hook override subtitles
        SubtitleWindowSettingsConstructorFingerprint.result?.let {
            it.mutableMethod.apply {
                addInstructions(
                    0,
                    """
                        invoke-static {p1, p2, p3, p4, p5}, $INTEGRATIONS_CLASS_DESCRIPTOR->getSubtitleWindowSettingsOverride(IIIZZ)[I
                        move-result-object v0
                        const/4 v1, 0x0
                        aget p1, v0, v1     # ap, anchor configuration
                        const/4 v1, 0x1
                        aget p2, v0, v1     # ah, horizontal anchor
                        const/4 v1, 0x2
                        aget p3, v0, v1     # av, vertical anchor
                    """
                )
            }
        } ?: return SubtitleWindowSettingsConstructorFingerprint.toErrorResult()

        return PatchResultSuccess()
    }

    companion object {
        const val INTEGRATIONS_CLASS_DESCRIPTOR = "Lapp/revanced/integrations/patches/SpoofSignatureVerificationPatch;"
    }
}
