package app.revanced.patches.youtube.misc.bottomsheet.hook

import app.revanced.extensions.exception
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patches.youtube.misc.bottomsheet.hook.fingerprints.CreateBottomSheetFingerprint
import app.revanced.patches.youtube.misc.integrations.IntegrationsPatch
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction

@Patch(dependencies = [IntegrationsPatch::class, BottomSheetHookResourcePatch::class])
@Suppress("unused")
object BottomSheetHookPatch : BytecodePatch(
    setOf(CreateBottomSheetFingerprint)
) {
    internal lateinit var addHook: (String) -> Unit
        private set

    override fun execute(context: BytecodeContext) {
        CreateBottomSheetFingerprint.result?.let {
            it.mutableMethod.apply {
                val returnLinearLayoutIndex = implementation!!.instructions.lastIndex
                val linearLayoutRegister = getInstruction<OneRegisterInstruction>(returnLinearLayoutIndex).registerA

                addHook = { classDescriptor ->
                    addInstruction(
                        returnLinearLayoutIndex,
                        "invoke-static { v$linearLayoutRegister }, " +
                                "${classDescriptor}->" +
                                "onFlyoutMenuCreate(Landroid/widget/LinearLayout;)V"
                    )
                }
            }
        } ?: throw CreateBottomSheetFingerprint.exception
    }
}