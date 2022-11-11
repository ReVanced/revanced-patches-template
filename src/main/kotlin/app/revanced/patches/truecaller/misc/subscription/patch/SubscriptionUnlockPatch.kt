package app.revanced.patches.truecaller.misc.subscription.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.addInstruction
import app.revanced.patcher.extensions.replaceInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.truecaller.misc.subscription.annotations.SubscriptionUnlockCompatibility
import app.revanced.patches.truecaller.misc.subscription.fingerprints.SubscriptionUnlockFingerprint
import app.revanced.patches.truecaller.misc.subscription.fingerprints.SubscriptionUnlockFingerprint2

@Patch
@Name("truecaller-subscription-unlock")
@Description("Unlocks all subscription features.")
@SubscriptionUnlockCompatibility
@Version("0.0.1")
class SubscriptionUnlockPatch : BytecodePatch(
    listOf(
        SubscriptionUnlockFingerprint,
        SubscriptionUnlockFingerprint2
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {

        val result = SubscriptionUnlockFingerprint.result!!
        val result2 = SubscriptionUnlockFingerprint2.result!!
        val method = result.mutableMethod
        val method2 = result2.mutableMethod

        val index = result.scanResult.stringsScanResult!!.matches[4].index + 4
        val index2 = result2.scanResult.stringsScanResult!!.matches[2].index + 48
        val index3 = method2.implementation!!.instructions.lastIndex - 2

        method.addInstruction(
            index,
            """
                const-wide v6, 0x7663A2E29C40L
            """
        )

        method.addInstruction(
            index + 12,
            """
                sget-object v1, Lcom/truecaller/premium/data/tier/PremiumTierType;->GOLD:Lcom/truecaller/premium/data/tier/PremiumTierType;
            """
        )

        method.addInstruction(
            index + 15,
            """
                sget-object v3, Lcom/truecaller/premium/data/ProductKind;->SUBSCRIPTION_GOLD:Lcom/truecaller/premium/data/ProductKind;
            """
        )

        method.addInstruction(
            index + 17,
            """
                sget-object v4, Lcom/truecaller/premium/data/PremiumScope;->PAID_PREMIUM:Lcom/truecaller/premium/data/PremiumScope;
            """
        )

        method.addInstruction(
            index + 20,
            """
                const/4 v1, 0x0
            """
        )

        method.addInstruction(
            index + 25,
            """
                const/4 v1, 0x0
            """
        )

        method.addInstruction(
            index + 27,
            """
                sget-object v5, Lcom/truecaller/premium/provider/Store;->GOOGLE_PLAY:Lcom/truecaller/premium/provider/Store;
            """
        )

        method2.replaceInstruction(
            index2,
            """
                sget-object p1, Ljava/lang/Boolean;->TRUE:Ljava/lang/Boolean;
            """
        )

        method2.addInstruction(
            index3,
            """
                const/4 v0, 0x1
            """
        )

        return PatchResultSuccess()
    }
}