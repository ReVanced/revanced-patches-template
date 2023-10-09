package app.revanced.patches.unifiprotect.dialog.devicenotfound

import app.revanced.extensions.exception
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patches.unifiprotect.dialog.devicenotfound.fingerprints.HideDeviceNotFoundDialogMethodFingerprint

@Patch(
    name = "Hide device not found dialog",
    compatiblePackages = [CompatiblePackage("com.ubnt.unifi.protect")]
)

object HideDeviceNotFoundPatch : BytecodePatch(
    setOf(HideDeviceNotFoundDialogMethodFingerprint)
) {
    override fun execute(context: BytecodeContext) {
        HideDeviceNotFoundDialogMethodFingerprint.result?.mutableMethod?.addInstruction(0, "return-void") ?: throw HideDeviceNotFoundDialogMethodFingerprint.exception
    }
}