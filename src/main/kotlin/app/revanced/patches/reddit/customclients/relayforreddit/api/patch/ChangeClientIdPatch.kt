package app.revanced.patches.reddit.customclients.relayforreddit.api.patch

import app.revanced.extensions.toErrorResult
import app.revanced.patcher.annotation.*
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.annotation.Package
import app.revanced.patcher.extensions.InstructionExtensions.replaceInstruction
import app.revanced.patcher.patch.OptionsContainer
import app.revanced.patcher.patch.PatchOption
import app.revanced.patcher.patch.PatchResultError
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.reddit.customclients.relayforreddit.api.fingerprints.GetClientIdFingerprint

@Patch
@Name("change-client-id")
@Description("Changes the OAuth client ID so Relay can be used after July 1st")
@Compatibility([Package("free.reddit.news"), Package("reddit.news")])
@Version("0.0.1")
class ChangeClientIdPatch :
    BytecodePatch(listOf(GetClientIdFingerprint)) {
    override fun execute(context: BytecodeContext): PatchResult {
        val result = GetClientIdFingerprint.result ?: return GetClientIdFingerprint.toErrorResult()
        val newClientId = clientId
            ?: return PatchResultError("You need to provide a client ID using patch options to use this patch")

        result.mutableMethod.replaceInstruction(0, "const-string v0, \"$newClientId\"")

        return PatchResultSuccess()
    }

    companion object : OptionsContainer() {
        var clientId: String? by option(
            PatchOption.StringOption(
                "client-id",
                null,
                "OAuth client ID",
                "Client ID for Reddit api"
            )
        )
    }
}