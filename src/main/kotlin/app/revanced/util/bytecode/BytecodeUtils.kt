package app.revanced.util.bytecode

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.util.proxy.mutableTypes.MutableClass
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import org.jf.dexlib2.Opcode
import org.jf.dexlib2.builder.instruction.BuilderInstruction21c
import org.jf.dexlib2.iface.ClassDef
import org.jf.dexlib2.iface.Method


internal object BytecodeUtils{
    public fun transformIns(context: BytecodeContext, transformFn: (Ins: Any, index: Int, methodDef: Method, classDef: ClassDef, context: BytecodeContext) -> Any, opcodes: List<Opcode>) {
        context.transformInstructions(transformFn, opcodes, context)
    }


    private fun BytecodeContext.transformInstructions(transformFn: (Ins: Any, index: Int, methodDef: Method, classDef: ClassDef, context: BytecodeContext) -> Any , opcodes: List<Opcode>, context:BytecodeContext) {
        classes.forEach { classDef ->

            // enumerate all methods
            classDef.methods.forEach classLoop@{ methodDef ->
                val implementation = methodDef.implementation ?: return@classLoop

                // enumerate all instructions and find invokes
                implementation.instructions.forEachIndexed implLoop@{ index, instruction ->
                    if (!opcodes.any{opcodes.contains(instruction.opcode)}) return@implLoop
                    transformFn(instruction, index, methodDef, classDef, context)
                }          
            }
        }
    }

    public fun makeMethodMutable(context: BytecodeContext, classDef: ClassDef, methodDef: Method): MutableMethod? {
        var mutableClass: MutableClass? = context.proxy(classDef).mutableClass
        var mutableMethod: MutableMethod? =  mutableClass!!.methods.first {
            it.name == methodDef.name && it.parameterTypes.containsAll(methodDef.parameterTypes)
        }
        
        return mutableMethod
    }
}

