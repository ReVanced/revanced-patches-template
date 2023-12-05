package app.revanced.patches.reddit.customclients.boostforreddit.api

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.fingerprint.MethodFingerprintResult
import app.revanced.patches.reddit.customclients.AbstractSpoofClientPatch
import app.revanced.patches.reddit.customclients.Constants.OAUTH_USER_AGENT
import app.revanced.patches.reddit.customclients.boostforreddit.api.fingerprints.GetClientIdFingerprint
import app.revanced.patches.reddit.customclients.boostforreddit.api.fingerprints.LoginActivityOnCreateFingerprint


@Suppress("unused")
object SpoofClientPatch : AbstractSpoofClientPatch(
    redirectUri = "http://rubenmayayo.com",
    clientIdFingerprints = setOf(GetClientIdFingerprint),
    userAgentFingerprints = setOf(LoginActivityOnCreateFingerprint),
    compatiblePackages = setOf(CompatiblePackage("com.rubenmayayo.reddit"))
) {
    override fun Set<MethodFingerprintResult>.patchClientId(context: BytecodeContext) {
        first().mutableMethod.addInstructions(
            0,
            """
                 const-string v0, "$clientId"
                 return-object v0
            """
        )
    }

    override fun Set<MethodFingerprintResult>.patchUserAgent(context: BytecodeContext) {
        first().let { result ->
            result.mutableMethod.apply {
                val insertIndex = result.scanResult.patternScanResult!!.endIndex

                addInstructions(
                    insertIndex,
                    """
                        const-string v7, "$OAUTH_USER_AGENT"
                        invoke-virtual {v4, v7}, Landroid/webkit/WebSettings;->setUserAgentString(Ljava/lang/String;)V
                    """
                )
            }
        }
    }
}
