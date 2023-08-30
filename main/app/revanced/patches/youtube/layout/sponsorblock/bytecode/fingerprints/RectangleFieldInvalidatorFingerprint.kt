package app.revanced.patches.youtube.layout.sponsorblock.bytecode.fingerprints


import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import org.jf.dexlib2.iface.instruction.ReferenceInstruction
import org.jf.dexlib2.iface.reference.MethodReference

object RectangleFieldInvalidatorFingerprint : MethodFingerprint(
    "V",
    customFingerprint = custom@{ methodDef, _ ->
        val instructions = methodDef.implementation?.instructions!!
        val instructionCount = instructions.count()

        // the method has definitely more than 5 instructions
        if (instructionCount < 5) return@custom false

        val referenceInstruction = instructions.elementAt(instructionCount - 2) // the second to last instruction
        val reference = ((referenceInstruction as? ReferenceInstruction)?.reference as? MethodReference)

        reference?.parameterTypes?.size == 1 && reference.name == "invalidate" // the reference is the invalidate(..) method
    }
)