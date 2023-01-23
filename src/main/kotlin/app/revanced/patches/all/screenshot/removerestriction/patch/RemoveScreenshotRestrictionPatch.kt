package app.revanced.patches.all.screenshot.removerestriction.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import app.revanced.util.patch.AbstractTransformInstructionsPatch
import app.revanced.util.patch.IMethodCall
import app.revanced.util.patch.fromMethodReference
import org.jf.dexlib2.Opcode
import org.jf.dexlib2.iface.ClassDef
import org.jf.dexlib2.iface.Method
import org.jf.dexlib2.iface.instruction.Instruction
import org.jf.dexlib2.iface.instruction.formats.Instruction35c
import org.jf.dexlib2.iface.reference.MethodReference
import java.util.*

private typealias InstructionInfo = Triple<RemoveScreenshotRestrictionPatch.MethodCall, Instruction35c, Int>

@Patch(false)
@Name("remove-screenshot-restriction")
@Description("Removes the restriction of making screenshots.")
@Version("0.0.1")
class RemoveScreenshotRestrictionPatch : AbstractTransformInstructionsPatch<InstructionInfo>() {

    private companion object {
        const val INTEGRATIONS_CLASS_DESCRIPTOR = "Lapp/revanced/all/screenshot/removerestriction/RemoveScreenshotRestrictionPatch;"
    }

    // Information about method calls we want to replace
    enum class MethodCall(
        override val definedClassName: String,
        override val methodName: String,
        override val methodParams: Array<String>,
        override val returnType: String
    ): IMethodCall {
        SetFlags(
            "Landroid/view/Window;",
            "setFlags",
            arrayOf("I", "I"),
            "V",
        );
    }

    override fun filterMap(
        classDef: ClassDef,
        method: Method,
        instruction: Instruction,
        instructionIndex: Int
    ): InstructionInfo? {
        if (classDef.type == INTEGRATIONS_CLASS_DESCRIPTOR) {
            // avoid infinite recursion
            return null
        }

        if (instruction.opcode != Opcode.INVOKE_VIRTUAL) {
            return null
        }

        val invokeInstruction = instruction as Instruction35c
        val methodRef = invokeInstruction.reference as MethodReference
        val methodCall = fromMethodReference<MethodCall>(methodRef) ?: return null

        return InstructionInfo(methodCall, invokeInstruction, instructionIndex)
    }

    override fun transform(mutableMethod: MutableMethod, entry: InstructionInfo) {
        val (methodType, instruction, instructionIndex) = entry
        methodType.replaceInvokeVirtualWithIntegrations(INTEGRATIONS_CLASS_DESCRIPTOR, mutableMethod, instruction, instructionIndex)
    }
}
