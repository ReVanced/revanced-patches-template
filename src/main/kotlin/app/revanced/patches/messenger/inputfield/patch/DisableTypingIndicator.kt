package app.revanced.patches.messenger.inputfield.patch

import app.revanced.extensions.error
import app.revanced.patcher.BytecodeContext
import app.revanced.patcher.annotation.Compatibility
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Package
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.extensions.InstructionExtensions.replaceInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.messenger.inputfield.fingerprints.SendTypingIndicatorFingerprint

@Patch
@Name("Disable typing indicator")
@Description("Disables the indicator while typing a message")
@Compatibility([Package("com.facebook.orca")])
@Version("0.0.1")
class DisableTypingIndicator : BytecodePatch(listOf(SendTypingIndicatorFingerprint)) {
    override suspend fun execute(context: BytecodeContext) {
        SendTypingIndicatorFingerprint.result?.mutableMethod?.replaceInstruction(0, "return-void")
            ?: SendTypingIndicatorFingerprint.error()
    }
}
