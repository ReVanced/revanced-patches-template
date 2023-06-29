package app.revanced.patches.youtube.layout.buttons.navigation.utils

import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import org.jf.dexlib2.Opcode.MOVE_RESULT_OBJECT
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction

internal object InjectionUtils {
    const val REGISTER_TEMPLATE_REPLACEMENT: String = "REGISTER_INDEX"

    /**
     * Injects an instruction into insertIndex of the hook.
     * @param hook The hook to insert.
     * @param insertIndex The index to insert the instruction at.
     * [MOVE_RESULT_OBJECT] has to be the previous instruction before [insertIndex].
     */
    fun MutableMethod.injectHook(hook: String, insertIndex: Int) {
        val injectTarget = this

        // Register to pass to the hook
        val registerIndex = insertIndex - 1 // MOVE_RESULT_OBJECT is always the previous instruction
        val register = injectTarget.getInstruction<OneRegisterInstruction>(registerIndex).registerA

        injectTarget.addInstruction(
            insertIndex,
            hook.replace("REGISTER_INDEX", register.toString()),
        )
    }
}