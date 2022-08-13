package app.revanced.patches.ecmwf.misc.subscription.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.impl.BytecodeData
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.extensions.removeInstruction
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.patch.impl.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patches.ecmwf.misc.subscription.annotations.SubscriptionUnlockCompatibility
import app.revanced.patches.ecmwf.misc.subscription.fingerprints.SubscriptionUnlockFingerprint

@Patch
@Name("subscription-unlock")
@Description("Unlocks all subscription features.")
@SubscriptionUnlockCompatibility
@Version("0.0.1")
class SubscriptionUnlockPatch : BytecodePatch(
    listOf(
        SubscriptionUnlockFingerprint
    )
) {
    override fun execute(data: BytecodeData): PatchResult {
        val result = SubscriptionUnlockFingerprint.result!!
        val method = result.mutableMethod


        // addInstructions needs an index which starts counting at 0 and size starts counting at 1
        val index = method.implementation!!.instructions.size

        // remove R() at 10212
        method.removeInstruction(index - 3)
        // remove R() at 10206
        method.removeInstruction(index - 5)

        val insertIndex = method.implementation!!.instructions.count()

        method.addInstructions(
            (insertIndex - 1), 
            """
                const/4 p1, 0x1
            """
        )
        return PatchResultSuccess()
    }
}