package app.revanced.patches.youtube.misc.fix.playback.patch

import app.revanced.extensions.toErrorResult
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.data.toMethodWalker
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import app.revanced.patches.youtube.misc.fix.playback.annotation.SpoofSignatureVerificationCompatibility
import app.revanced.patches.youtube.misc.fix.playback.fingerprints.SpoofSignatureVerificationFingerprint

@Patch
@Name("spoof-signature-verification")
@Description("Spoofs the client to prevent playback issues.")
@SpoofSignatureVerificationCompatibility
@Version("0.0.1")
class SpoofSignatureVerificationPatch : BytecodePatch(
    listOf(SpoofSignatureVerificationFingerprint)
) {
    override fun execute(context: BytecodeContext): PatchResult {
        SpoofSignatureVerificationFingerprint.result?.let {
            val setParamMethod = context
                .toMethodWalker(it.method)
                    .nextMethod(it.scanResult.patternScanResult!!.startIndex, true).getMethod() as MutableMethod

            setParamMethod.apply {
                val protobufParameterRegister = 3

                addInstructions(
                    0,
                    """
                        invoke-static {p$protobufParameterRegister}, $INTEGRATIONS_CLASS_DESCRIPTOR->getVerificationSpoofOverride(Ljava/lang/String;)Ljava/lang/String;
                        move-result-object p$protobufParameterRegister
                    """
                )
            }
        } ?: return SpoofSignatureVerificationFingerprint.toErrorResult()

        return PatchResultSuccess()
    }

    companion object {
        const val INTEGRATIONS_CLASS_DESCRIPTOR = "Lapp/revanced/integrations/patches/SpoofSignatureVerificationPatch;"
    }
}
