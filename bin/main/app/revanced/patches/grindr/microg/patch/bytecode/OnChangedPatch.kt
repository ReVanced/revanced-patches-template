package app.revanced.patches.grindr.microg.patch.bytecode

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.extensions.toErrorResult

import app.revanced.patches.grindr.microg.fingerprints.OnChangedFingerprint

import app.revanced.patcher.extensions.InstructionExtensions.replaceInstructions

class OnChangedPatch : BytecodePatch(
    listOf(
        OnChangedFingerprint,
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {
        OnChangedFingerprint.result?.let { result ->
            println("Found fingerprint!")

            result.mutableMethod.apply {
                replaceInstructions(
                    81,
                    """
                        nop
                    """
                )
            }
        } ?: return OnChangedFingerprint.toErrorResult()

        return PatchResultSuccess()
    }
}