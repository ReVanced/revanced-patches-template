package app.revanced.patches.shared.misc.fix.spoof.patch

import app.revanced.patcher.BytecodeContext
import app.revanced.extensions.error
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.extensions.addInstruction
import app.revanced.patcher.extensions.instruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchException
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.shared.misc.fix.spoof.annotations.ClientSpoofCompatibility
import app.revanced.patches.shared.misc.fix.spoof.fingerprints.UserAgentHeaderBuilderFingerprint
import org.jf.dexlib2.iface.instruction.FiveRegisterInstruction

@Patch
@Name("client-spoof")
@Description("Spoofs a patched client to allow playback.")
@ClientSpoofCompatibility
@Version("0.0.1")
class ClientSpoofPatch : BytecodePatch(
    listOf(UserAgentHeaderBuilderFingerprint)
) {
    override fun execute(context: BytecodeContext) {
        UserAgentHeaderBuilderFingerprint.result?.let { result ->
            val insertIndex = result.scanResult.patternScanResult!!.endIndex
           result.mutableMethod.apply {
               val packageNameRegister = (instruction(insertIndex) as FiveRegisterInstruction).registerD
               addInstruction(insertIndex, "const-string v$packageNameRegister, \"$ORIGINAL_PACKAGE_NAME\"")
           }

        } ?: return UserAgentHeaderBuilderFingerprint.error()

    }

    private companion object {
        private const val ORIGINAL_PACKAGE_NAME = "com.google.android.youtube"
    }
}
