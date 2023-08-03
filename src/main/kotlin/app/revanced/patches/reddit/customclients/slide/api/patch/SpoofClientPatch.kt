package app.revanced.patches.reddit.customclients.slide.api.patch

import app.revanced.patcher.annotation.Compatibility
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Package
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprintResult
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patches.reddit.customclients.AbstractSpoofClientPatch
import app.revanced.patches.reddit.customclients.SpoofClientAnnotation
import app.revanced.patches.reddit.customclients.boostforreddit.api.fingerprints.GetClientIdFingerprint

@SpoofClientAnnotation
@Description("Spoofs the client in order to allow logging in. " +
        "The OAuth application type has to be \"Installed app\" " +
        "and the redirect URI has to be set to \"http://www.ccrama.me\".")
@Compatibility([Package("me.ccrama.redditslide")])
class SpoofClientPatch : AbstractSpoofClientPatch(
    "http://www.ccrama.me", Options, listOf(GetClientIdFingerprint)
) {
    override fun List<MethodFingerprintResult>.patchClientId(context: BytecodeContext): PatchResult {
        first().mutableMethod.addInstructions(
            0,
    """
                     const-string v0, "$clientId"
                     return-object v0
                """
        )

        return PatchResultSuccess()
    }

    companion object Options : AbstractSpoofClientPatch.Options.SpoofClientOptionsContainer()
}
