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
import app.revanced.patches.youtube.misc.fix.playback.fingerprints.ConnectionResultFingerprint
import app.revanced.patches.youtube.misc.fix.playback.fingerprints.ProtobufParameterBuilderFingerprint
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch
import app.revanced.patches.youtube.misc.video.information.patch.VideoInformationPatch
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction

@Patch
@Name("spoof-signature-verification")
@Description("Spoofs the client to prevent playback issues.")
@ProtobufSpoofCompatibility
@DependsOn([IntegrationsPatch::class, SettingsPatch::class, VideoInformationPatch::class])
@Version("0.0.1")
class SpoofSignatureVerificationPatch : BytecodePatch(
    listOf(
        ProtobufParameterBuilderFingerprint,
        ConnectionResultFingerprint,
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {
        ProtobufParameterBuilderFingerprint.result?.let {
            val setParamMethod = context
                .toMethodWalker(it.method)
                    .nextMethod(it.scanResult.patternScanResult!!.startIndex, true).getMethod() as MutableMethod

            setParamMethod.apply {
                val protobufParameterRegister = 3

                addInstructions(
                    0,
                    """
                        invoke-static {p$protobufParameterRegister}, $INTEGRATIONS_CLASS_DESCRIPTOR->getProtoBufParameterOverride(Ljava/lang/String;)Ljava/lang/String;
                        move-result-object p$protobufParameterRegister
                    """
                )
            }
        } ?: return ProtobufParameterBuilderFingerprint.toErrorResult()

        ConnectionResultFingerprint.result?.let {
            val method = it.mutableMethod
            val endIndex = it.scanResult.patternScanResult!!.endIndex
            val statusCodeRegister = (method.instruction(endIndex - 2) as OneRegisterInstruction).registerA
            val urlHeadersRegister = (method.instruction(endIndex) as OneRegisterInstruction).registerA

            method.addInstructions(
                endIndex + 1,
                """
                    invoke-static {v$statusCodeRegister, v$urlHeadersRegister}, $INTEGRATIONS_CLASS_DESCRIPTOR->connectionCompleted(ILjava/util/Map;)V
                """
            )
        } ?: return ConnectionResultFingerprint.toErrorResult()

        SettingsPatch.PreferenceScreen.MISC.addPreferences(
            SwitchPreference(
                "revanced_force_signature_spoofing",
                StringResource("revanced_force_signature_spoofing_title", "Force app signature spoofing"),
                false,
                StringResource("revanced_force_signature_spoofing_summary_on", "Signature always spoofed"),
                StringResource("revanced_force_signature_spoofing_summary_off", "Signature automatically spoofed when needed")
            )
        )

        return PatchResultSuccess()
    }

    companion object {
        const val INTEGRATIONS_CLASS_DESCRIPTOR = "Lapp/revanced/integrations/patches/SpoofSignatureVerificationPatch;"
    }
}
