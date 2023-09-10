package app.revanced.patches.candylinkvpn

import app.revanced.extensions.exception
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patches.candylinkvpn.fingerprints.IsPremiumPurchasedFingerprint

@Patch(
    name = "Unlock premium",
    description = "Unlocks premium features.",
    compatiblePackages = [CompatiblePackage("com.candylink.openvpn")]
)
object UnlockProPatch : BytecodePatch(
    setOf(IsPremiumPurchasedFingerprint)
) {
    override fun execute(context: BytecodeContext) {
        IsPremiumPurchasedFingerprint.result?.mutableMethod?.addInstructions(
            0,
            """
               const/4 v0, 0x1
               return v0
            """
        ) ?: throw IsPremiumPurchasedFingerprint.exception
    }
}