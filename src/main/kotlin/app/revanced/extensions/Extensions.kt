package app.revanced.extensions

import app.revanced.patcher.BytecodeContext
import app.revanced.patcher.util.proxy.mutableTypes.MutableClass
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import app.revanced.patcher.util.smali.toInstruction
import org.jf.dexlib2.builder.MutableMethodImplementation

/**
 * traverse the class hierarchy starting from the given root class
 *
 * @param targetClass the class to start traversing the class hierarchy from
 * @param callback function that is called for every class in the hierarchy
 */
fun BytecodeContext.traverseClassHierarchy(targetClass: MutableClass, callback: MutableClass.() -> Unit) {
    callback(targetClass)
    this.classes.findClassProxied(targetClass.superclass ?: return)?.mutableClass?.let {
        traverseClassHierarchy(it, callback)
    }
}

/**
 * apply a transform to all methods of the class
 *
 * @param transform the transformation function. original method goes in, transformed method goes out
 */
fun MutableClass.transformMethods(transform: MutableMethod.() -> MutableMethod) {
    val transformedMethods = methods.map { it.transform() }
    methods.clear()
    methods.addAll(transformedMethods)
}