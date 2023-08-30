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

import app.revanced.patches.grindr.microg.fingerprints.GetPackageNameFingerprint

import app.revanced.patcher.extensions.InstructionExtensions.addInstructions

import app.revanced.patches.grindr.microg.Constants.PACKAGE_NAME
class GetPackageNamePatch : BytecodePatch(
    listOf(
        GetPackageNameFingerprint,
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {
        GetPackageNameFingerprint.result?.let { result ->
            println("Found get package name fingerprint!")

            result.mutableMethod.apply {
                addInstructions(
                    0,
                    """
                        const-string v0, "$PACKAGE_NAME"
                        return-object v0
                    """
                )
            }
        } ?: return GetPackageNameFingerprint.toErrorResult()

        return PatchResultSuccess()
    }
}