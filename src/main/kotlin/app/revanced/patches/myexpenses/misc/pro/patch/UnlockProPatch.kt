package app.revanced.patches.myexpenses.misc.pro.patch

import app.revanced.patcher.BytecodeContext
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.myexpenses.misc.pro.annotations.UnlockProCompatibility
import app.revanced.patches.myexpenses.misc.pro.fingerprints.IsEnabledFingerprint

@Patch
@Name("Unlock pro")
@Description("Unlocks all professional features.")
@UnlockProCompatibility
@Version("0.0.1")
class UnlockProPatch : BytecodePatch(
    listOf(
        IsEnabledFingerprint
    )
) {
    override suspend fun execute(context: BytecodeContext) {
        val method = IsEnabledFingerprint.result!!.mutableMethod
        method.addInstructions(
            0,
            """
                const/4 v0, 0x1
                return v0
            """
        )

    }
}
