package app.revanced.patches.reddit.misc.tracking.url.patch

import app.revanced.extensions.toErrorResult
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.extensions.instruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.reddit.misc.tracking.url.annotations.SanitizeUrlQueryCompatibility
import app.revanced.patches.reddit.misc.tracking.url.fingerprints.ShareLinkFactoryFingerprint
import org.jf.dexlib2.iface.instruction.Instruction
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction

@Patch
@Name("sanitize-sharing-links")
@Description("Removes (tracking) query parameters from the URLs when sharing links.")
@SanitizeUrlQueryCompatibility
@Version("0.0.1")
class SanitizeUrlQueryPatch : BytecodePatch(
    listOf(ShareLinkFactoryFingerprint)
) {
    override fun execute(context: BytecodeContext): PatchResult {
        ShareLinkFactoryFingerprint.result?.let {
            it.mutableMethod.apply {
                val patternScan = it.scanResult.patternScanResult!!

                val startIndex = patternScan.startIndex
                val endIndex = patternScan.endIndex

                val freeRegister1 = instruction(startIndex).registerA
                val freeRegister2 = instruction(startIndex + 1).registerA

                val urlRegister = instruction(endIndex).registerA

                val insertIndex = endIndex + 1 // Right after URL is moved into a register.
                addInstructions(
                    insertIndex,
                    """
                        invoke-static {v$urlRegister}, Lapp/revanced/reddit/patches/SanitizeUrlQueryPatch;->removeTrackingParameters(Ljava/lang/String;)Ljava/lang/String;
  move-result-object v$urlRegister
                   """
                )
            }
        } ?: return ShareLinkFactoryFingerprint.toErrorResult()

        return PatchResultSuccess()
    }

    private companion object {
        val Instruction.registerA
            get() = (this as OneRegisterInstruction).registerA
    }
}
