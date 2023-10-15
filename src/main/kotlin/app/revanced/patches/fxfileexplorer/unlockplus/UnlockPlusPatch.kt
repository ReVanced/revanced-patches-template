package app.revanced.patches.fxfileexplorer.unlockplus

import app.revanced.extensions.exception
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patches.fxfileexplorer.unlockplus.fingerprints.IsPlusUnlockedFingerprint

@Patch(
    name = "Unlock FX Plus",
    description = "Unlock features like 'Web Access', 'Network' and 'FX Connect'.",
    compatiblePackages = [CompatiblePackage("nextapp.fx")]
)

@Suppress("unused")
object UnlockPlusPatch : BytecodePatch(
    setOf(IsPlusUnlockedFingerprint)
) {
    override fun execute(context: BytecodeContext) = IsPlusUnlockedFingerprint.result?.mutableMethod?.addInstructions(
        0,
        """
            const/4 v0, 0x1
            return v0
        """
    ) ?: throw IsPlusUnlockedFingerprint.exception
}
