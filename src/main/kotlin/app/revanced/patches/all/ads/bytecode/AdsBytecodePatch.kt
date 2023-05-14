package app.revanced.patches.all.ads.bytecode

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.util.bytecode.*
import app.revanced.patches.all.ads.blocklist.*
import app.revanced.patcher.util.proxy.mutableTypes.MutableClass
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import org.jf.dexlib2.Opcode
import org.jf.dexlib2.iface.instruction.formats.Instruction21c
import org.jf.dexlib2.iface.instruction.formats.Instruction35c
import org.jf.dexlib2.iface.reference.MethodReference
import org.jf.dexlib2.iface.reference.StringReference
import org.jf.dexlib2.immutable.reference.ImmutableStringReference
import org.jf.dexlib2.iface.ClassDef
import org.jf.dexlib2.iface.Method
import org.jf.dexlib2.builder.instruction.BuilderInstruction21c
import org.jf.dexlib2.builder.instruction.BuilderInstruction10x


@Patch
@Name("ads")
@Description("Removes all ads from the selected application.")
@Version("0.0.1")
class AdsBytecodePatch : BytecodePatch() {
    override fun execute(context: BytecodeContext): PatchResult {
        BytecodeUtils.transformIns(context, ::invokesTransformFunction, invokeOpcodes)
        BytecodeUtils.transformIns(context, ::urlTransformFunction, stringOpcodes)
        return PatchResultSuccess()
    }

    private fun invokesTransformFunction(Ins: Any, index: Int, methodDef: Method, classDef: ClassDef, context: BytecodeContext) {
        if (Ins is Instruction35c) {
            val instructionMethodReference = Ins.getReference() as MethodReference

            if (blockInvokes.any{blockInvokes.contains(instructionMethodReference.getName())}) {
                
                // make class and method mutable, if not already
                var mutableClass: MutableClass? = context.proxy(classDef).mutableClass
                var mutableMethod: MutableMethod? =  mutableClass!!.methods.first {
                    it.name == methodDef.name && it.parameterTypes.containsAll(methodDef.parameterTypes)
                }

                mutableMethod!!.implementation!!.replaceInstruction(
                    index,
                    BuilderInstruction10x(
                        Opcode.NOP
                    )
                )
            } 
        }
    }

    private fun urlTransformFunction(Ins: Any, index: Int, methodDef: Method, classDef: ClassDef, context: BytecodeContext) {
        if (Ins is Instruction21c) {
            val str = (Ins.reference as StringReference).string
            if (blockUrls.any{blockUrls.contains(str)}) {

                // make class and method mutable, if not already
                var mutableClass: MutableClass? = context.proxy(classDef).mutableClass
                var mutableMethod: MutableMethod? =  mutableClass!!.methods.first {
                    it.name == methodDef.name && it.parameterTypes.containsAll(methodDef.parameterTypes)
                }

                // replace instruction with updated string
                mutableMethod!!.implementation!!.replaceInstruction(
                    index,
                    BuilderInstruction21c(
                        Opcode.CONST_STRING,
                        Ins.registerA,
                        ImmutableStringReference(
                            replaceUrlsWith
                        )
                    )
                )
            } 
        }
    }
}
