package app.revanced.patches.anytracker.misc.premium.patch


import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.anytracker.misc.premium.annotations.UnlockPremiumCompatibility
import app.revanced.patches.anytracker.misc.premium.fingerprints.IsPurchasedFlowFingerprint

@Patch
@Name("unlock-premium")
@Description("Unlocks all premium features.")
@UnlockPremiumCompatibility
@Version("0.0.1")
class UnlockPremiumPatch : BytecodePatch(
    listOf(
        IsPurchasedFlowFingerprint
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {
        val method = IsPurchasedFlowFingerprint.result!!.mutableMethod
        method.addInstructions(
            0,
            """
                const/4 p1, 0x1
                invoke-static {p1}, Ljava/lang/Boolean;->valueOf(Z)Ljava/lang/Boolean;
                move-result-object p1
                invoke-static {p1}, Lkotlinx/coroutines/flow/FlowKt;->flowOf(Ljava/lang/Object;)Lkotlinx/coroutines/flow/Flow;
            """
        )

        return PatchResultSuccess()
    }
}