package app.revanced.patches.all.ads.bytecode

import app.revanced.patcher.annotation.*
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.util.patch.AbstractTransformInstructionsPatch
import app.revanced.patcher.util.proxy.mutableTypes.MutableClass
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import app.revanced.util.patch.*
import app.revanced.patches.all.ads.blocklist.*
import org.jf.dexlib2.Opcode
import org.jf.dexlib2.iface.ClassDef
import org.jf.dexlib2.iface.Method
import org.jf.dexlib2.iface.instruction.Instruction
import org.jf.dexlib2.iface.reference.MethodReference
import org.jf.dexlib2.builder.instruction.BuilderInstruction10x
import org.jf.dexlib2.iface.instruction.formats.Instruction35c
import java.util.*

@Patch(false)
@Name("remove-ads")
@Description("Attempts to remove ads.")
@Version("0.0.1")
internal class RemoveAdsPatch : AbstractTransformInstructionsPatch<Instruction35cInfo>() {

    enum class MethodCall(
        override val definedClassName: String,
        override val methodName: String,
        override val methodParams: Array<String>,
        override val returnType: String,
    ): IMethodCall {
        loadAd(
            "Lcom/google/android/gms/ads/BaseAdView;",
            "loadAd",
            arrayOf("Lcom/google/android/gms/ads/AdRequest;"),
            "V",
        ),
        loadAd1(
            "Lcom/google/android/gms/ads/AdLoader;",
            "loadAd",
            arrayOf("Lcom/google/android/gms/ads/AdRequest;"),
            "V",
        )
    }


    override fun filterMap(
        classDef: ClassDef,
        method: Method,
        instruction: Instruction,
        instructionIndex: Int
    ) = filterMapInstruction35c<MethodCall>(
        "a",
        classDef,
        instruction,
        instructionIndex
    )

    override fun transform(mutableMethod: MutableMethod, entry: Instruction35cInfo) {
        val implementation = mutableMethod.implementation ?: return

        // enumerate all instructions and find invokes
        implementation.instructions.forEachIndexed implLoop@{ index, instruction ->
            if (!invokeOpcodes.any{ it == instruction.opcode}) return@implLoop
            val ins = instruction as Instruction35c
            val instructionMethodReference = ins.getReference() as MethodReference
            println(instructionMethodReference.getName())
            mutableMethod.implementation!!.replaceInstruction(
                index,
                BuilderInstruction10x(
                    Opcode.NOP
                )
            )
            
        }    
        
    }
}
