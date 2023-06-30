package app.revanced.extensions

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.MethodFingerprintExtensions.name
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint.Companion.resolve
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprintResult
import app.revanced.patcher.patch.PatchResultError
import app.revanced.patcher.util.proxy.mutableTypes.MutableClass
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import app.revanced.patches.shared.mapping.misc.patch.ResourceMappingPatch
import org.jf.dexlib2.Opcode
import org.jf.dexlib2.iface.ClassDef
import org.jf.dexlib2.iface.Method
import org.jf.dexlib2.iface.instruction.WideLiteralInstruction
import org.jf.dexlib2.util.MethodUtil
import org.w3c.dom.Node

// TODO: populate this to all patches
/**
 * Convert a [MethodFingerprint] to a [PatchResultError].
 *
 * @return A [PatchResultError] for the [MethodFingerprint].
 */
internal fun MethodFingerprint.toErrorResult() = PatchResultError("Failed to resolve $name")

fun MethodFingerprint.resolveMany(context: BytecodeContext, classes: Iterable<ClassDef>) = sequence {
    for (classDef in classes) // search through all classes for the fingerprint
        yieldAll(this@resolveMany.resolveMany(context, classDef))

}
/**
 * Resolve a [MethodFingerprint] against all methods of a [ClassDef].
 *
 * @param forClass The class on which to resolve the [MethodFingerprint] in.
 * @param context The [BytecodeContext] to host proxies.
 * @return A sequence of [MethodFingerprintResult].
 */
fun MethodFingerprint.resolveMany(context: BytecodeContext, forClass: ClassDef) = sequence {
    for (method in forClass.methods)
        if (this@resolveMany.resolve(context, method, forClass))
            yield(this@resolveMany.result!!.also { this@resolveMany.result = null })
}

/**
 * Find the [MutableMethod] from a given [Method] in a [MutableClass].
 *
 * @param method The [Method] to find.
 * @return The [MutableMethod].
 */
internal fun MutableClass.findMutableMethodOf(method: Method) = this.methods.first {
    MethodUtil.methodSignaturesMatch(it, method)
}

/**
 * apply a transform to all methods of the class
 *
 * @param transform the transformation function. original method goes in, transformed method goes out
 */
internal fun MutableClass.transformMethods(transform: MutableMethod.() -> MutableMethod) {
    val transformedMethods = methods.map { it.transform() }
    methods.clear()
    methods.addAll(transformedMethods)
}

internal fun Node.doRecursively(action: (Node) -> Unit) {
    action(this)
    for (i in 0 until this.childNodes.length) this.childNodes.item(i).doRecursively(action)
}

internal fun MutableMethod.injectHideViewCall(
    insertIndex: Int,
    viewRegister: Int,
    classDescriptor: String,
    targetMethod: String
) = addInstruction(
    insertIndex,
    "invoke-static { v$viewRegister }, $classDescriptor->$targetMethod(Landroid/view/View;)V"
)

internal fun Method.findIndexForIdResource(resourceName: String): Int {
    fun getIdResourceId(resourceName: String) = ResourceMappingPatch.resourceMappings.single {
        it.type == "id" && it.name == resourceName
    }.id

    return indexOfFirstConstantInstructionValue(getIdResourceId(resourceName))
}

/**
 * @return the first constant instruction with the value, or -1 if not found.
 */
fun Method.indexOfFirstConstantInstructionValue(constantValue: Long): Int {
    return implementation?.let {
        it.instructions.indexOfFirst { instruction ->
            instruction.opcode == Opcode.CONST && (instruction as WideLiteralInstruction).wideLiteral == constantValue
        }
    } ?: -1
}

/**
 * @return if the method contains a constant with the given value.
 */
fun Method.containsConstantInstructionValue(constantValue: Long): Boolean {
    return indexOfFirstConstantInstructionValue(constantValue) >= 0
}
