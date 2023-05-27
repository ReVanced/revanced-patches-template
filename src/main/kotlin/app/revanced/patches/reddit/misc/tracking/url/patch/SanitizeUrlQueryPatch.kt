package app.revanced.patches.reddit.misc.tracking.url.patch

import app.revanced.extensions.error
import app.revanced.patcher.BytecodeContext
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.extensions.instruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.patch.annotations.RequiresIntegrations
import app.revanced.patches.reddit.misc.tracking.url.annotations.SanitizeUrlQueryCompatibility
import app.revanced.patches.reddit.misc.tracking.url.fingerprints.ShareLinkFactoryFingerprint
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction

@Patch
@Name("sanitize-sharing-links")
@Description("Removes (tracking) query parameters from the URLs when sharing links.")
@SanitizeUrlQueryCompatibility
@Version("0.0.1")
@RequiresIntegrations
class SanitizeUrlQueryPatch : BytecodePatch(
    listOf(ShareLinkFactoryFingerprint)
) {
    override fun execute(context: BytecodeContext) {
        ShareLinkFactoryFingerprint.result?.let { result ->
            result.mutableMethod.apply {
                val insertIndex = result.scanResult.patternScanResult!!.endIndex + 1
                val urlRegister = instruction<OneRegisterInstruction>(insertIndex - 1).registerA

                addInstructions(
                    insertIndex,
                    """
                        invoke-static {v$urlRegister}, $SANITIZE_METHOD_DESCRIPTOR
                        move-result-object v$urlRegister
                   """
                )
            }
        } ?: ShareLinkFactoryFingerprint.error()
    }

    private companion object {
        private const val SANITIZE_METHOD_DESCRIPTOR =
            "Lapp/revanced/reddit/patches/SanitizeUrlQueryPatch;" +
                    "->stripQueryParameters(Ljava/lang/String;)Ljava/lang/String;"
    }
}
