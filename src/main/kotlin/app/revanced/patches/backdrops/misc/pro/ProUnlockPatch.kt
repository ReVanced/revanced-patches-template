package app.revanced.patches.backdrops.misc.pro

import app.revanced.extensions.exception
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patches.backdrops.misc.pro.fingerprints.ProUnlockFingerprint
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction

@Patch(
    name = "Pro unlock",
    compatiblePackages = [CompatiblePackage("com.backdrops.wallpapers", ["4.52"])]
)
@Suppress("unused")
object ProUnlockPatch : BytecodePatch(
    setOf(ProUnlockFingerprint)
) {
    override fun execute(context: BytecodeContext) {
        ProUnlockFingerprint.result?.let { result ->
            val registerIndex = result.scanResult.patternScanResult!!.endIndex - 1

            result.mutableMethod.apply {
                val register = getInstruction<OneRegisterInstruction>(registerIndex).registerA
                addInstruction(
                    result.scanResult.patternScanResult!!.endIndex,
                    """
                        const/4 v$register, 0x1
                    """
                )
            }

        } ?: throw ProUnlockFingerprint.exception
    }
}