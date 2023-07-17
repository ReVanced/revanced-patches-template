package app.revanced.patches.reddit.customclients.slide.api.patch

import app.revanced.patcher.BytecodeContext
import app.revanced.patcher.annotation.Compatibility
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Package
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprintResult
import app.revanced.patches.reddit.customclients.AbstractChangeOAuthClientIdPatch
import app.revanced.patches.reddit.customclients.ChangeOAuthClientIdPatchAnnotation
import app.revanced.patches.reddit.customclients.boostforreddit.api.fingerprints.GetClientIdFingerprint

@ChangeOAuthClientIdPatchAnnotation
@Description("Changes the OAuth client ID. " +
        "The OAuth application type has to be \"Installed app\" " +
        "and the redirect URI has to be set to \"http://www.ccrama.me\".")
@Compatibility([Package("me.ccrama.redditslide")])
class ChangeOAuthClientIdPatch : AbstractChangeOAuthClientIdPatch(
    "http://www.ccrama.me", Options, listOf(GetClientIdFingerprint)
) {
    override fun List<MethodFingerprintResult>.patch(context: BytecodeContext) {
        first().mutableMethod.addInstructions(
            0,
    """
                     const-string v0, "$clientId"
                     return-object v0
                """
        )
    }

    companion object Options : AbstractChangeOAuthClientIdPatch.Options.ChangeOAuthClientIdOptionsContainer()
}
