package app.revanced.patches.nova.prime.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.addInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.nova.prime.annotations.UnlockPrimeCompatibility
import app.revanced.patches.nova.prime.fingerprints.UnlockPrimeFingerprint
import app.revanced.patches.tann.dice.unlock.fingerprints.UnlockFullFingerprint

@Patch
@Name("unlock-prime")
@Description("Unlock Nova Prime.")
@UnlockPrimeCompatibility
@Version("0.0.1")
class UnlockPrimePatch : BytecodePatch(
    listOf(
        UnlockPrimeFingerprint
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {
        val method = UnlockFullFingerprint.result!!.mutableMethod

        method.addInstruction(
            8,
            """
                const/16 p1, 0x512
            """
        )

        return PatchResultSuccess()
    }
}
