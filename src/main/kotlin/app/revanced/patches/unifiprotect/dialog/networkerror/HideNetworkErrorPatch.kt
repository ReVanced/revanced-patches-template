package app.revanced.patches.unifiprotect.dialog.networkerror

import app.revanced.extensions.exception
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patches.unifiprotect.dialog.networkerror.fingerprints.ShowVpnErrorDialogMethodFingerprint

@Patch(
    name = "Hide network error dialog",
    description = "Hides the network error dialog that appears when the app is unable to connect to the internet. This is useful for people who use the app on a local network without internet access.",
    compatiblePackages = [CompatiblePackage("com.ubnt.unifi.protect")]
)

object HideNetworkErrorPatch : BytecodePatch(
    setOf(ShowVpnErrorDialogMethodFingerprint)
) {
    override fun execute(context: BytecodeContext) =
        ShowVpnErrorDialogMethodFingerprint.result?.mutableMethod?.addInstruction(0, "return-void")
            ?: throw ShowVpnErrorDialogMethodFingerprint.exception
}