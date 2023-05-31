package app.revanced.patches.vsco.misc.pro.patch

import app.revanced.extensions.error
import app.revanced.patcher.BytecodeContext
import app.revanced.patcher.annotation.*
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.vsco.misc.pro.fingerprints.RevCatSubscriptionFingerprint


@Patch
@Name("unlock-pro")
@Description("Unlocks pro features.")
@Compatibility([Package("com.vsco.cam")])
@Version("0.0.1")
class UnlockProPatch : BytecodePatch(
    listOf(RevCatSubscriptionFingerprint)
) {
    override suspend fun execute(context: BytecodeContext) {
        RevCatSubscriptionFingerprint.result?.mutableMethod?.apply {
            // Set isSubscribed to true.
            addInstructions(
                0,
                """
                    const p1, 0x1
                """
            )
        } ?: RevCatSubscriptionFingerprint.error()
    }
}
