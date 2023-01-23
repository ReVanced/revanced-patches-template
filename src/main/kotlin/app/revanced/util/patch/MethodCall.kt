package app.revanced.util.patch

import app.revanced.patcher.extensions.replaceInstruction
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import org.jf.dexlib2.iface.instruction.formats.Instruction35c
import org.jf.dexlib2.iface.reference.MethodReference

interface IMethodCall {
    val definedClassName: String
    val methodName: String
    val methodParams: Array<String>
    val returnType: String

    /**
     * Replaces an invoke-virtual instruction with an invoke-static instruction,
     * which calls a static replacement method in the respective integrations class.
     * The method definition in the integrations class is expected to be the same,
     * except that the method should be static and take as a first parameter
     * an instance of the class, in which the original method was defined in.
     *
     * Example:
     *
     * original method: Window#setFlags(int, int)
     *
     * replacement method: Integrations#setFlags(Window, int, int)
     */
    fun replaceInvokeVirtualWithIntegrations(integrationsClassDescriptor: String, method: MutableMethod, instruction: Instruction35c, instructionIndex: Int) {
        val registers = arrayOf(instruction.registerC, instruction.registerD, instruction.registerE, instruction.registerF, instruction.registerG)
        val argsNum = methodParams.size + 1 // + 1 for instance of definedClassName
        if (argsNum > registers.size) {
            // should never happen, but just to be sure (also for the future) a safety check
            throw RuntimeException("not enough registers for ${definedClassName}#${methodName}: required $argsNum registers, but only got ${registers.size}")
        }
        val args = registers.take(argsNum).joinToString(separator = ", ") { reg -> "v${reg}" }
        val replacementMethodDefinition = "${methodName}(${definedClassName}${methodParams.joinToString(separator = "")})${returnType}"
        method.replaceInstruction(
            instructionIndex,
            "invoke-static { $args }, ${integrationsClassDescriptor}->${replacementMethodDefinition}"
        )
    }
}

inline fun <reified E> fromMethodReference(methodReference: MethodReference)
        where E: Enum<E>, E: IMethodCall = enumValues<E>().firstOrNull { search ->
    search.definedClassName == methodReference.definingClass
            && search.methodName == methodReference.name
            && methodReference.parameterTypes.toTypedArray().contentEquals(search.methodParams)
}
