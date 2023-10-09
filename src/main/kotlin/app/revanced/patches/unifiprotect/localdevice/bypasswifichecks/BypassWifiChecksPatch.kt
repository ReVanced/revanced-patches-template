package app.revanced.patches.unifiprotect.localdevice.bypasswifichecks

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchException
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patches.unifiprotect.localdevice.bypasswifichecks.fingerprints.IsWifiMethodFingerprint

@Patch(
    name = "Bypass wifi checks",
    description = "Bypasses the wifi checks that prevent the app from connecting to local devices.",
    compatiblePackages = [CompatiblePackage("com.ubnt.unifi.protect")],
)

object BypassWifiChecksPatch : BytecodePatch(
    setOf(IsWifiMethodFingerprint)
) {
    override fun execute(context: BytecodeContext) =
        IsWifiMethodFingerprint.result?.mutableMethod?.addInstructions(
            0, """
                const/4 v0, 0x1
                return v0
            """
        ) ?: throw PatchException("Could not find method to patch")

}