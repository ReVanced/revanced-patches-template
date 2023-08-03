package app.revanced.patches.lightroom.misc.login.patch

import app.revanced.extensions.toErrorResult
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.replaceInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.lightroom.misc.login.annotations.BypassLoginCompatibility
import app.revanced.patches.lightroom.misc.login.fingerprint.BypassLoginFingerprint

@Patch
@Name("Bypass login")
@Description("Bypasses the requirement to login.")
@BypassLoginCompatibility
class BypassLoginPatch : BytecodePatch(listOf(BypassLoginFingerprint)) {
    override fun execute(context: BytecodeContext): PatchResult {
        BypassLoginFingerprint.result?.mutableMethod?.apply {
            val index = implementation!!.instructions.lastIndex - 1
            replaceInstruction(index, "const/4 v0, 0x1")
        } ?: throw BypassLoginFingerprint.toErrorResult()

        return PatchResultSuccess()
    }
}