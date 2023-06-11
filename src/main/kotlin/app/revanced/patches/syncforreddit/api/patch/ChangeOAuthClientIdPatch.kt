package app.revanced.patches.syncforreddit.api.patch

import app.revanced.patcher.annotation.*
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.extensions.InstructionExtensions.replaceInstruction
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint.Companion.resolve
import app.revanced.patcher.patch.*
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.syncforreddit.api.fingerprints.GetAuthorizationStringFingerprint
import app.revanced.patches.syncforreddit.api.fingerprints.GetBearerTokenFingerprint
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction
import org.jf.dexlib2.iface.instruction.ReferenceInstruction
import org.jf.dexlib2.iface.reference.StringReference
import java.util.*

@Patch
@Name("change-oauth-client-id")
@Description("Changes the OAuth client ID.")
@Compatibility([Package("com.laurencedawson.reddit_sync")])
@Version("0.0.1")
class ChangeOAuthClientIdPatch : BytecodePatch(
    listOf(GetAuthorizationStringFingerprint)
) {
    override fun execute(context: BytecodeContext): PatchResult {
        if (clientId == null) return PatchResultError("No client ID provided.")

        GetAuthorizationStringFingerprint.result?.also {
            if (!GetBearerTokenFingerprint.resolve(context, it.classDef))
                return PatchResultError("Could not find required method to patch.")

            GetBearerTokenFingerprint.result!!.mutableMethod.apply {
                val auth = Base64.getEncoder().encodeToString("$clientId:".toByteArray(Charsets.UTF_8))
                addInstructions(
                    0,
                    """
                         const-string v0, "Basic $auth"
                         return-object v0
                    """
                )
            }
        }?.let {
            val occurrenceIndex = it.scanResult.stringsScanResult!!.matches.first().index

            it.mutableMethod.apply {
                val authorizationStringInstruction = getInstruction<ReferenceInstruction>(occurrenceIndex)
                val targetRegister = (authorizationStringInstruction as OneRegisterInstruction).registerA
                val reference = authorizationStringInstruction.reference as StringReference

                val newAuthorizationUrl = reference.string.replace(
                    "client_id=.*?&".toRegex(),
                    "client_id=${clientId!!}&"
                )

                replaceInstruction(
                    occurrenceIndex,
                    "const-string v$targetRegister, \"$newAuthorizationUrl\""
                )
            }
        } ?: return PatchResultError("Could not find required method to patch.")
        return PatchResultSuccess()
    }

    companion object : OptionsContainer() {
        val clientId by option(
            PatchOption.StringOption(
                "client-id",
                null,
                "OAuth client ID",
                "The client ID to use for OAuth."
            )
        )
    }
}