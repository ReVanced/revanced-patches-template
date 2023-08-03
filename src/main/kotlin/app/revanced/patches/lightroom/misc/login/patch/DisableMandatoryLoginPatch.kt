package app.revanced.patches.lightroom.misc.login.patch

import app.revanced.extensions.toErrorResult
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.replaceInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.lightroom.misc.login.annotations.DisableMandatoryLoginCompatibility
import app.revanced.patches.lightroom.misc.login.fingerprint.DisableMandatoryLoginFingerprint

@Patch
@Name("Disable mandatory login")
@DisableMandatoryLoginCompatibility
class DisableMandatoryLoginPatch : BytecodePatch(listOf(DisableMandatoryLoginFingerprint)) {
    override fun execute(context: BytecodeContext): PatchResult {
        DisableMandatoryLoginFingerprint.result?.mutableMethod?.apply {
            val index = implementation!!.instructions.lastIndex - 1
            // Set isLoggedIn = true.
            replaceInstruction(index, "const/4 v0, 0x1")
        } ?: throw DisableMandatoryLoginFingerprint.toErrorResult()

        return PatchResultSuccess()
    }
}