package app.revanced.patches.googlemaps.censorship

import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.extensions.InstructionExtensions.removeInstruction
import app.revanced.patcher.extensions.InstructionExtensions.replaceInstruction
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import app.revanced.util.patch.AbstractTransformInstructionsPatch
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.ClassDef
import com.android.tools.smali.dexlib2.iface.Method
import com.android.tools.smali.dexlib2.iface.instruction.Instruction
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.reference.MethodReference

@Patch(
    name = "Uncensored satellite view",
    description = "Circumvent Google's intentional provision of low-resolution satellite imagery in select regions through spoofing the country information provided by the SIM card.",
    compatiblePackages = [CompatiblePackage("com.google.android.apps.maps")]
)
@Suppress("unused")
object UncensoredSatelliteViewPatch : AbstractTransformInstructionsPatch<Int>() {
    override fun filterMap(
        classDef: ClassDef, method: Method, instruction: Instruction, instructionIndex: Int
    ) = filterCountryCodeMethodCall(instruction, instructionIndex)

    override fun transform(mutableMethod: MutableMethod, entry: Int) {
        // Get register of the next MOVE_RESULT_OBJECT instruction that accompanies INVOKE_VIRTUAL
        val register = mutableMethod.getInstruction<OneRegisterInstruction>(entry + 1).registerA

        mutableMethod.removeInstruction(entry + 1)
        // We return Iceland's ISO code, I don't see a reason to customize this for now
        mutableMethod.replaceInstruction(entry, "const-string v$register, \"is\"")
    }

    private fun filterCountryCodeMethodCall(
        instruction: Instruction, instructionIndex: Int
    ): Int? {
        if (instruction.opcode != Opcode.INVOKE_VIRTUAL) return null
        if (instruction !is ReferenceInstruction) return null
        val reference = instruction.reference as? MethodReference ?: return null

        if (reference.definingClass != "Landroid/telephony/TelephonyManager;") return null
        if (reference.name != "getSimCountryIso" && reference.name != "getNetworkCountryIso") return null

        return instructionIndex
    }
}
