package app.revanced.extensions

import org.jf.dexlib2.iface.instruction.ReferenceInstruction
import org.jf.dexlib2.iface.reference.MethodReference
import java.util.*

object InstructionExtensions {
    enum class MethodReferenceMatch {
        DEFINING_CLASS, NAME, PARAMTER_TYPES, RETURN_TYPE;

        companion object {
            val all: EnumSet<MethodReferenceMatch> = EnumSet.allOf(MethodReferenceMatch::class.java)
        }
    }

    fun ReferenceInstruction.referenceEquals(
        methodReference: MethodReference,
        match: EnumSet<MethodReferenceMatch> = MethodReferenceMatch.all
    ): Boolean {
        val ref = (reference as? MethodReference) ?: return false

        if (match.contains(MethodReferenceMatch.DEFINING_CLASS) && ref.definingClass != methodReference.definingClass) return false
        if (match.contains(MethodReferenceMatch.NAME) && ref.name != methodReference.name) return false
        if (match.contains(MethodReferenceMatch.PARAMTER_TYPES) && ref.parameterTypes != methodReference.parameterTypes) return false
        if (match.contains(MethodReferenceMatch.RETURN_TYPE) && ref.returnType != methodReference.returnType) return false

        return true
    }
}
