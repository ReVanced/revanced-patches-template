package app.revanced.patches.vsco.misc.pro.patch

import app.revanced.extensions.toErrorResult
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patches.vsco.misc.pro.annotations.ProUnlockCompatibility
import app.revanced.patches.vsco.misc.pro.fingerprints.RevCatSubscriptionFingerprint


@Patch
@Name("pro-unlock")
@Description("Unlock Pro functions.")
@ProUnlockCompatibility
@Version("0.0.1")
class ProUnlockPatch : BytecodePatch(
    listOf(RevCatSubscriptionFingerprint)
) {
    override fun execute(context: BytecodeContext): PatchResult {
        RevCatSubscriptionFingerprint.result?.mutableMethod?.apply {
            addInstructions(
                0,
                """
                    # Set isSubscribed param to True
                    const p1, 0x1
                """
            )
        } ?: return RevCatSubscriptionFingerprint.toErrorResult()

        return PatchResultSuccess()
    }
}
