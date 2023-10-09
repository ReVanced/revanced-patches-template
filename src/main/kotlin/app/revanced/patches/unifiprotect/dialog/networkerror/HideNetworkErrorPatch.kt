package app.revanced.patches.unifiprotect.dialog.networkerror

import app.revanced.extensions.exception
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patches.unifiprotect.dialog.networkerror.fingerprints.HideNetworkErrorDialogMethodFingerprint

@Patch(
    name = "Hide network error dialog",
    compatiblePackages = [CompatiblePackage("com.ubnt.unifi.protect")]
)

object HideNetworkErrorPatch : BytecodePatch(
    setOf(HideNetworkErrorDialogMethodFingerprint)
) {
    override fun execute(context: BytecodeContext) {
        HideNetworkErrorDialogMethodFingerprint.result?.mutableMethod?.addInstruction(0, "return-void") ?: throw HideNetworkErrorDialogMethodFingerprint.exception
    }
}