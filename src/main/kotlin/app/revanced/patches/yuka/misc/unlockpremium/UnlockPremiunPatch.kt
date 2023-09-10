package app.revanced.patches.yuka.misc.unlockpremium

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint.Companion.resolve
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patches.yuka.misc.unlockpremium.annotations.UnlockPremiumCompatibility
import app.revanced.patches.yuka.misc.unlockpremium.fingerprints.IsPremiumFingerprint
import app.revanced.patches.yuka.misc.unlockpremium.fingerprints.YukaUserConstructorFingerprint

@Patch(
    name = "Unlock premium",
    description = "Unlocks premium features."
)
@UnlockPremiumCompatibility
@Suppress("unused")
object UnlockPremiunPatch : BytecodePatch(
    setOf(
        YukaUserConstructorFingerprint
    )
) {

    override fun execute(context: BytecodeContext) {
        IsPremiumFingerprint.resolve(context,YukaUserConstructorFingerprint.result!!.classDef)
        val method = IsPremiumFingerprint.result!!.mutableMethod
        method.addInstructions(
            0,
            """
                const/4 v0, 0x1
                return v0
            """
        )
    }

}