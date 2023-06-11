package app.revanced.util.fingerprint

import app.revanced.extensions.InstructionExtensions
import app.revanced.extensions.InstructionExtensions.referenceEquals
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.util.fingerprint.FingerprintUtils.hasInstruction
import org.jf.dexlib2.Opcode
import org.jf.dexlib2.iface.instruction.ReferenceInstruction
import org.jf.dexlib2.iface.reference.MethodReference
import java.util.*

abstract class MethodWithReferenceInstructionFingerprint(
    internal val returnType: String? = null,
    internal val accessFlags: Int? = null,
    internal val parameters: Iterable<String>? = null,
    internal val opcodes: Iterable<Opcode?>? = null,
    internal val strings: Iterable<String>? = null,
    instructionOpcode: Opcode,
    methodReference: MethodReference,
    matchSettings: EnumSet<InstructionExtensions.MethodReferenceMatch> = InstructionExtensions.MethodReferenceMatch.all
) : MethodFingerprint(
    returnType,
    accessFlags,
    parameters,
    opcodes,
    strings,
    customFingerprint = hasInstruction<ReferenceInstruction>(instructionOpcode) {
        it.referenceEquals(methodReference, matchSettings)
    }
)
