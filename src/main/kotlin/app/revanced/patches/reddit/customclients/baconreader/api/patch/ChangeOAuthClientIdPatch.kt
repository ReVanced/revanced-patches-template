package app.revanced.patches.reddit.customclients.baconreader.api.patch

import app.revanced.patcher.annotation.Compatibility
import app.revanced.patcher.annotation.Package
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.extensions.InstructionExtensions.replaceInstruction
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprintResult
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.util.proxy.mutableTypes.encodedValue.MutableStringEncodedValue
import app.revanced.patches.reddit.customclients.AbstractChangeOAuthClientIdPatch
import app.revanced.patches.reddit.customclients.ChangeOAuthClientIdPatchAnnotation
import app.revanced.patches.reddit.customclients.baconreader.api.fingerprints.RequestTokenFingerprint
import app.revanced.patches.reddit.customclients.baconreader.api.fingerprints.GetClientIdFingerprint
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction


@ChangeOAuthClientIdPatchAnnotation
@Compatibility([Package("com.onelouder.baconreader")])
class ChangeOAuthClientIdPatch : AbstractChangeOAuthClientIdPatch(
    "http://baconreader.com/auth", Options, listOf(GetClientIdFingerprint, RequestTokenFingerprint)
) {

    override fun List<MethodFingerprintResult>.patch(context: BytecodeContext): PatchResult {

        GetClientIdFingerprint.result?.let {
            val clientIdIndex = it.scanResult.stringsScanResult!!.matches.first().index

            it.mutableMethod.apply {
                val clientIdRegister = getInstruction<OneRegisterInstruction>(clientIdIndex).registerA
                replaceInstruction(
                    clientIdIndex,
                    "const-string v$clientIdRegister, \"client_id=$clientId\""
                )
            }
        }

        RequestTokenFingerprint.result?.let {
            it.mutableClass.apply {
                fields.find { field -> field.name == "APP_ID" }?.apply {
                    val value = this.initialValue as MutableStringEncodedValue
                    value.value = clientId!!
                }
            }
        }

        RequestTokenFingerprint.result?.let {
            val clientIdIndex = it.scanResult.stringsScanResult!!.matches.first().index

            it.mutableMethod.apply {
                val clientIdRegister = getInstruction<OneRegisterInstruction>(clientIdIndex).registerA
                replaceInstruction(
                    clientIdIndex,
                    "const-string v$clientIdRegister, \"$clientId\""
                )
            }
        }

        return PatchResultSuccess()
    }

    companion object Options : AbstractChangeOAuthClientIdPatch.Options.ChangeOAuthClientIdOptionsContainer()
}
