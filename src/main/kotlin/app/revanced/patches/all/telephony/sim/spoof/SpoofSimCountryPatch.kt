package app.revanced.patches.all.telephony.sim.spoof

import android.os.Environment
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.extensions.InstructionExtensions.removeInstruction
import app.revanced.patcher.extensions.InstructionExtensions.replaceInstruction
import app.revanced.patcher.patch.PatchException
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patcher.patch.options.PatchOptionException
import app.revanced.patcher.patch.options.types.StringPatchOption.Companion.stringPatchOption
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import app.revanced.util.patch.AbstractTransformInstructionsPatch
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction11x
import com.android.tools.smali.dexlib2.iface.ClassDef
import com.android.tools.smali.dexlib2.iface.Method
import com.android.tools.smali.dexlib2.iface.instruction.Instruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.reference.MethodReference
import java.io.File
import java.util.*


@Patch(
    name = "Spoof SIM country",
    description = "Spoofs country information returned by the SIM card provider.",
    use = false,
)
@Suppress("unused")
object SpoofSimCountryPatch : AbstractTransformInstructionsPatch<Pair<Int, String>>() {
    private val networkCountryIso by stringPatchOption(
        "networkIsoCountryCode",
        null,
        "Network ISO Country Code",
        "ISO-3166-1 alpha-2 country code equivalent of the MCC (Mobile Country Code) " +
                "of the current registered operator or the cell nearby.",
        validator = { it?.uppercase() in Locale.getISOCountries() || it == null }
    )

    private val simCountryIso by stringPatchOption(
        "simIsoCountryCode",
        null,
        "Sim ISO Country Code",
        "ISO-3166-1 alpha-2 country code equivalent for the SIM provider's country code.",
        validator = { it?.uppercase() in Locale.getISOCountries() || it == null }
    )

    override fun execute(context: BytecodeContext) {
        loadPatchOptionsForAndroid()

        super.execute(context)
    }

    override fun filterMap(
        classDef: ClassDef,
        method: Method,
        instruction: Instruction,
        instructionIndex: Int
    ) = filterMethodCall(instruction, instructionIndex)

    override fun transform(
        mutableMethod: MutableMethod,
        entry: Pair<Int, String>
    ) = transformMethodCall(entry, mutableMethod)

    private fun filterMethodCall(
        instruction: Instruction,
        instructionIndex: Int
    ): Pair<Int, String>? {
        if (instruction.opcode != Opcode.INVOKE_VIRTUAL) return null
        if (instruction !is ReferenceInstruction) return null

        val reference = instruction.reference as? MethodReference ?: return null
        if (reference.definingClass != "Landroid/telephony/TelephonyManager;") return null

        if (!options.contains(reference.name)) return null
        return (options[reference.name].value as? String)?.let { instructionIndex to it.trim() }
    }

    private fun transformMethodCall(
        entry: Pair<Int, String>,
        mutableMethod: MutableMethod
    ) {
        val (instructionIndex, methodCallValue) = entry

        // Get register of the next MOVE_RESULT_OBJECT instruction that accompanies INVOKE_VIRTUAL
        val instruction = mutableMethod.getInstruction(instructionIndex + 1)
        val register = (instruction as? BuilderInstruction11x ?: throw PatchException(
            """
            This shouldn't happen.
            Expected a `move-result-object` instruction at location ${instructionIndex + 1}
            ClassName: ${mutableMethod.definingClass}
            MethodName: ${mutableMethod.name}
            """.trimIndent()
        )).registerA

        mutableMethod.removeInstruction(instructionIndex + 1)
        mutableMethod.replaceInstruction(instructionIndex, "const-string v$register, \"$methodCallValue\"")
    }

    private fun loadPatchOptionsForAndroid() {
        val isAndroid = try {
            Class.forName("android.os.Environment")
            true
        } catch (_: ClassNotFoundException) {
            false
        }

        if (isAndroid) {
            val properties = Properties()

            val propertiesFile = File(
                Environment.getExternalStorageDirectory(), "revanced_simcountry_spoof.properties"
            )
            if (propertiesFile.exists()) {
                properties.load(propertiesFile.inputStream())

                // Set options from properties file.
                properties.forEach { (name, value) ->
                    try {
                        options[name.toString()] =
                            value.toString().trim().takeIf { it.isNotBlank() && it.length <= 32 }
                    } catch (_: PatchOptionException.PatchOptionNotFoundException) {
                        // Ignore unknown options.
                    }
                }
            } else {
                options.values.forEach {
                    properties.setProperty(it.key, it.value as? String ?: "")
                }

                properties.store(
                    propertiesFile.outputStream(),
                    """
                    Options for the ReVanced "Spoof SIM country" patch.
                    Omitted options or options with blank values are ignored.
                    """.trimIndent()
                )

                val error =
                    """
                    A properties file has been created at ${propertiesFile.absolutePath}.
                    Provide the following options and run the patch again:
                    ${options.values.joinToString("\n\n") { "${it.key}: ${it.description}" }}
                    """.trimIndent()

                throw PatchException(error)
            }
        }
    }
}
