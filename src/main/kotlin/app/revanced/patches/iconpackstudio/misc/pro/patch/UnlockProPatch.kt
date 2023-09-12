package app.revanced.patches.iconpackstudio.misc.pro.patch

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patches.iconpackstudio.misc.pro.fingerprints.CheckProFingerprint

@Patch(
    name = "Unlock pro",
    compatiblePackages = [CompatiblePackage("ginlemon.iconpackstudio")]
)
@Suppress("unused")
object UnlockProPatch : BytecodePatch(
    setOf(CheckProFingerprint)
) {
    override fun execute(context: BytecodeContext) {
        val method = CheckProFingerprint.result!!.mutableMethod
        method.addInstructions(
            0,
            """
                const/4 v0, 0x1
                return v0
            """
        )
    }
}
