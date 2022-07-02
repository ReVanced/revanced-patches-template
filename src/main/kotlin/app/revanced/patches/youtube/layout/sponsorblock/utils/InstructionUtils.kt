package app.revanced.patches.youtube.layout.sponsorblock.utils

import org.jf.dexlib2.iface.instruction.Instruction
import org.jf.dexlib2.iface.instruction.ReferenceInstruction
import org.jf.dexlib2.iface.reference.MethodReference

internal object InstructionUtils {
    fun Iterable<Instruction>.findInstructionsByName(name: String) = this.filter {
        it is ReferenceInstruction && (it.reference as MethodReference).name == name
    }
}