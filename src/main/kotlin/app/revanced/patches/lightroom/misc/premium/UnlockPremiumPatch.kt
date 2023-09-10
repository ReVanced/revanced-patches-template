package app.revanced.patches.lightroom.misc.premium

import app.revanced.extensions.exception
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.replaceInstruction
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patches.lightroom.misc.premium.fingerprint.HasPurchasedFingerprint

@Patch(
    name = "Unlock premium",
    description = "Unlocks premium features.",
    compatiblePackages = [CompatiblePackage("com.adobe.lrmobile")]
)
object UnlockPremiumPatch : BytecodePatch(listOf(HasPurchasedFingerprint)) {
    override fun execute(context: BytecodeContext) {
         // Set hasPremium = true.
        HasPurchasedFingerprint.result?.mutableMethod?.replaceInstruction(2, "const/4 v2, 0x1")
            ?: throw HasPurchasedFingerprint.exception
    }
}