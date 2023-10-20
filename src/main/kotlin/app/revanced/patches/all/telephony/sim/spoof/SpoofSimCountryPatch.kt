package app.revanced.patches.all.telephony.sim.spoof

import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.extensions.InstructionExtensions.replaceInstruction
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patcher.patch.options.types.StringPatchOption.Companion.stringPatchOption
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import app.revanced.util.patch.AbstractTransformInstructionsPatch
import com.android.tools.smali.dexlib2.iface.ClassDef
import com.android.tools.smali.dexlib2.iface.Method
import com.android.tools.smali.dexlib2.iface.instruction.Instruction
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.reference.MethodReference
import java.util.*


@Patch(
    name = "Spoof SIM country",
    description = "Spoofs country information returned by the SIM card provider.",
    use = false,
)
@Suppress("unused")
object SpoofSimCountryPatch : AbstractTransformInstructionsPatch<Pair<Int, String>>() {
    private val networkCountryIso by stringPatchOption(
        "networkCountryIso",
        null,
        "Network ISO Country Code",
        "ISO-3166-1 alpha-2 country code equivalent of the MCC (Mobile Country Code) " +
                "of the current registered operator or the cell nearby.",
        validator = { it?.uppercase() in Locale.getISOCountries() || it == null }
    )

    private val simCountryIso by stringPatchOption(
        "simCountryIso",
        null,
        "Sim ISO Country Code",
        "ISO-3166-1 alpha-2 country code equivalent for the SIM provider's country code.",
        validator = { it?.uppercase() in Locale.getISOCountries() || it == null }
    )

    override fun filterMap(
        classDef: ClassDef,
        method: Method,
        instruction: Instruction,
        instructionIndex: Int
    ): Pair<Int, String>? {
        if (instruction !is ReferenceInstruction) return null

        val methodRef = instruction.reference as? MethodReference ?: return null

        val methodMatch = MethodCall.entries.firstOrNull { search ->
            search.definedClassName == methodRef.definingClass
                    && search.methodName == methodRef.name
                    && methodRef.parameterTypes.toTypedArray().contentEquals(search.methodParams)
        } ?: return null

        val iso = when (methodMatch) {
            MethodCall.NetworkCountryIso -> networkCountryIso
            MethodCall.SimCountryIso -> simCountryIso
        }?.lowercase()

        return iso?.let { instructionIndex to it }
    }

    override fun transform(
        mutableMethod: MutableMethod,
        entry: Pair<Int, String>
    ) = transformMethodCall(entry, mutableMethod)

    private fun transformMethodCall(
        entry: Pair<Int, String>,
        mutableMethod: MutableMethod
    ) {
        val (instructionIndex, methodCallValue) = entry

        val register = mutableMethod.getInstruction<OneRegisterInstruction>(instructionIndex + 1).registerA

        mutableMethod.replaceInstruction(instructionIndex + 1, "const-string v$register, \"$methodCallValue\"")
    }

    private enum class MethodCall(
        val definedClassName: String,
        val methodName: String,
        val methodParams: Array<String>,
        val returnType: String
    ) {
        NetworkCountryIso(
            "Landroid/telephony/TelephonyManager;",
            "getNetworkCountryIso",
            emptyArray(),
            "Ljava/lang/String;"
        ),
        SimCountryIso(
            "Landroid/telephony/TelephonyManager;",
            "getSimCountryIso",
            emptyArray(),
            "Ljava/lang/String;"
        )
    }
}
