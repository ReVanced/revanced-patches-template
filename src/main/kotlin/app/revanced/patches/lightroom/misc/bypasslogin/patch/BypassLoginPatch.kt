package app.revanced.patches.lightroom.misc.bypasslogin.patch

import app.revanced.extensions.toErrorResult
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.replaceInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.lightroom.misc.bypasslogin.annotations.BypassLoginCompatibility
import app.revanced.patches.lightroom.misc.bypasslogin.fingerprint.BypassLoginFingerprint

@Patch
@Name("Bypass login")
@Description("Bypasses the login requirement")
@BypassLoginCompatibility
class BypassLoginPatch : BytecodePatch(
    listOf(
        BypassLoginFingerprint
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {
        BypassLoginFingerprint.result?.apply {
            val targetIndex = mutableMethod.implementation?.instructions?.lastIndex?.minus(1) ?: throw BypassLoginFingerprint.toErrorResult()
            mutableMethod.replaceInstruction(targetIndex,
                """
                    const/4 v0, 0x1 
                """
            )
        } ?: throw BypassLoginFingerprint.toErrorResult()

        return PatchResultSuccess()
    }
}