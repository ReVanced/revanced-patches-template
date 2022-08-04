package app.revanced.patches.warnwetter.misc.promocode.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.impl.BytecodeData
import app.revanced.patcher.extensions.removeInstruction
import app.revanced.patcher.extensions.removeInstructions
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.patch.impl.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patches.warnwetter.misc.promocode.annotations.PromoCodeUnlockCompatibility
import app.revanced.patches.warnwetter.misc.promocode.fingerprints.PromoCodeUnlockFingerprint

@Patch
@Name("promo-code-unlock")
@Description("Disable the validation of promo code, any string will work to unlock all features.")
@PromoCodeUnlockCompatibility
@Version("0.0.1")
class PromoCodeUnlockPatch : BytecodePatch(
    listOf(
        PromoCodeUnlockFingerprint
    )
) {

    override fun execute(data: BytecodeData): PatchResult {
        PromoCodeUnlockFingerprint.result!!.mutableMethod.removeInstructions(0, PromoCodeUnlockFingerprint.result!!.mutableMethod.implementation!!.instructions.size - 1)
        PromoCodeUnlockFingerprint.result!!.mutableMethod.addInstructions(
            0,
            """
                const/4 v0, 0x1
                return v0
            """
        )

        return PatchResultSuccess()
    }


}