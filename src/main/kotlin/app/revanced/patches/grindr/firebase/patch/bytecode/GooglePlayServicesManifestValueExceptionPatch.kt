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

import app.revanced.patches.grindr.microg.fingerprints.GooglePlayServicesManifestValueExceptionFingerprint

import app.revanced.patcher.extensions.InstructionExtensions.replaceInstructions

class GooglePlayServicesManifestValueExceptionPatch : BytecodePatch(
    listOf(
        GooglePlayServicesManifestValueExceptionFingerprint,
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {
        GooglePlayServicesManifestValueExceptionFingerprint.result?.let { result ->
            println("Found fingerprint!")

            result.mutableMethod.apply {
                replaceInstructions(
                    0,
                    """
                        const/4 v0, 1
                        return v0
                    """
                )
            }
        } ?: return GooglePlayServicesManifestValueExceptionFingerprint.toErrorResult()

        return PatchResultSuccess()
    }
}