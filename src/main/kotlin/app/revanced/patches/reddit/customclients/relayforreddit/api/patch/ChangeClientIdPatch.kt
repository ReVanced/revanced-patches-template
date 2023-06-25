package app.revanced.patches.reddit.customclients.relayforreddit.api.patch

import app.revanced.extensions.toErrorResult
import app.revanced.patcher.annotation.*
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.annotation.Package
import app.revanced.patcher.extensions.InstructionExtensions.replaceInstruction
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patcher.patch.OptionsContainer
import app.revanced.patcher.patch.PatchOption
import app.revanced.patcher.patch.PatchResultError
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.reddit.customclients.AbstractChangeOAuthClientIdPatch
import app.revanced.patches.reddit.customclients.relayforreddit.api.fingerprints.GetClientIdFingerprint

@Patch
@Name("change-oauth-client-id")
@Description("Changes the OAuth client ID.")
@Compatibility([Package("free.reddit.news"), Package("reddit.news")])
@Version("0.0.1")
class ChangeOAuthClientIdPatch : AbstractChangeOAuthClientIdPatch(
    "dbrady://relay",
    Options,
    GetClientIdFingerprint
) {
    override fun MethodFingerprint.patch(context: BytecodeContext): PatchResult {
        val result = GetClientIdFingerprint.result?.mutableMethod?.replaceInstruction(0, "const-string v0, \"$clientId\"")
            ?: return GetClientIdFingerprint.toErrorResult()

        return PatchResultSuccess()
    }

    companion object Options : AbstractChangeOAuthClientIdPatch.Options.ChangeOAuthClientIdOptionsContainer()
}