package app.revanced.patches.youtube.misc.fix.playback.patch

import app.revanced.extensions.toErrorResult
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.data.toMethodWalker
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import app.revanced.patches.shared.settings.preference.impl.StringResource
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.youtube.misc.fix.playback.fingerprints.OpenCronetDataSourceFingerprint
import app.revanced.patches.youtube.misc.fix.playback.fingerprints.ProtobufParameterBuilderFingerprint
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
import app.revanced.patches.youtube.misc.playertype.patch.PlayerTypeHookPatch
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch
import app.revanced.patches.youtube.video.videoid.patch.VideoIdPatch
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction

@Name("spoof-signature-verification")
@Description("Spoofs a patched client to prevent playback issues.")
@DependsOn([
    IntegrationsPatch::class,
    SettingsPatch::class,
    PlayerTypeHookPatch::class,
    VideoIdPatch::class
])
@Version("0.0.1")
class SpoofSignatureVerificationPatch : BytecodePatch(
    listOf(
        ProtobufParameterBuilderFingerprint,
        OpenCronetDataSourceFingerprint,
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {
        SettingsPatch.PreferenceScreen.MISC.addPreferences(
            SwitchPreference(
                "revanced_spoof_signature_verification",
                StringResource("revanced_spoof_signature_verification_title", "Spoof app signature"),
                StringResource("revanced_spoof_signature_verification_summary_on",
                    "App signature spoofed\\n\\n"
                        + "Side effects include:\\n"
                        + "• Ambient mode may not work\\n"
                        + "• Seekbar thumbnails are hidden\\n"
                        + "• Downloading videos may not work"),
                StringResource("revanced_spoof_signature_verification_summary_off", "App signature not spoofed"),
                StringResource("revanced_spoof_signature_verification_user_dialog_message",
                    "Turning off this setting may cause playback issues.")
            )
        )

        // Hook video id, required for subtitle fix.
        VideoIdPatch.injectCall("$INTEGRATIONS_CLASS_DESCRIPTOR->setCurrentVideoId(Ljava/lang/String;)V")

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
                    (getInstruction(getHeadersInstructionIndex - 2) as OneRegisterInstruction).registerA

                addInstructions(
                    getHeadersInstructionIndex + 1,
                    """
                        invoke-static {v$responseCodeRegister}, $INTEGRATIONS_CLASS_DESCRIPTOR->onResponse(I)V
                    """
                )
            }

        } ?: return OpenCronetDataSourceFingerprint.toErrorResult()

        return PatchResultSuccess()
    }

    private companion object {
        const val INTEGRATIONS_CLASS_DESCRIPTOR = "Lapp/revanced/integrations/patches/SpoofSignatureVerificationPatch;"
    }
}
