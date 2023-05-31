package app.revanced.patches.shared.misc.fix.verticalscroll.patch

import app.revanced.extensions.error
import app.revanced.patcher.BytecodeContext
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.extensions.addInstruction
import app.revanced.patcher.extensions.instruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patches.shared.misc.fix.verticalscroll.annotations.VerticalScrollCompatibility
import app.revanced.patches.shared.misc.fix.verticalscroll.fingerprints.CanScrollVerticallyFingerprint
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction

@Description("Fixes issues with refreshing the feed when the first component is of type EmptyComponent.")
@VerticalScrollCompatibility
@Version("0.0.1")
class VerticalScrollPatch : BytecodePatch(
    listOf(CanScrollVerticallyFingerprint)
) {
    override suspend fun execute(context: BytecodeContext) {
        CanScrollVerticallyFingerprint.result?.let {
            it.mutableMethod.apply {
                val moveResultIndex = it.scanResult.patternScanResult!!.endIndex
                val moveResultRegister = instruction<OneRegisterInstruction>(moveResultIndex).registerA

                val insertIndex = moveResultIndex + 1
                addInstruction(
                    insertIndex,
                    "const/4 v$moveResultRegister, 0x0"
                )
            }
        } ?: return CanScrollVerticallyFingerprint.error()

    }
}
