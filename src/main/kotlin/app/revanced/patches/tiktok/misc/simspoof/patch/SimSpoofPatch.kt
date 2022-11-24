package app.revanced.patches.tiktok.misc.simspoof.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.addInstruction
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.extensions.instruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.util.proxy.mutableTypes.MutableClass
import app.revanced.patches.tiktok.misc.integrations.patch.TikTokIntegrationsPatch
import app.revanced.patches.tiktok.misc.settings.fingerprints.SettingsStatusLoadFingerprint
import app.revanced.patches.tiktok.misc.settings.patch.TikTokSettingsPatch
import app.revanced.patches.tiktok.misc.simspoof.annotations.SimSpoofCompatibility
import org.jf.dexlib2.Opcode
import org.jf.dexlib2.iface.ClassDef
import org.jf.dexlib2.iface.Method
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction
import org.jf.dexlib2.iface.instruction.formats.Instruction35c
import org.jf.dexlib2.iface.reference.MethodReference
import org.jf.dexlib2.immutable.reference.ImmutableMethodReference

@Patch(false)
@DependsOn([TikTokIntegrationsPatch::class, TikTokSettingsPatch::class])
@Name("sim-spoof")
@Description("Fakes the sim card information to bypass region restrictions.")
@SimSpoofCompatibility
@Version("0.0.1")
class SimSpoofPatch : BytecodePatch() {

    internal companion object {
        private val PATCH_METHOD = hashMapOf(
            "getSimCountryIso" to "getCountryIso",
            "getNetworkCountryIso" to "getCountryIso",
            "getSimOperator" to "getOperator",
            "getNetworkOperator" to "getOperator",
            "getSimOperatorName" to "getOperatorName",
            "getNetworkOperatorName" to "getOperatorName"
        )

        private fun MutableClass.findMutableMethodOf(
            method: Method
        ) = this.methods.first {
            it.compareTo(
                ImmutableMethodReference(
                    method.definingClass, method.name, method.parameters, method.returnType
                )
            ) == 0
        }
    }

    override fun execute(context: BytecodeContext): PatchResult {
        //Find all api call to check sim information
        context.classes.forEach { classDef ->
            classDef.methods.forEach { method ->
                with(method.implementation) {
                    val patchIndexes = ArrayDeque<Int>()
                    this?.instructions?.forEachIndexed { index, instruction ->
                        if (instruction.opcode != Opcode.INVOKE_VIRTUAL) return@forEachIndexed
                        val methodRef = (instruction as Instruction35c).reference as MethodReference
                        if (methodRef.definingClass != "Landroid/telephony/TelephonyManager;") return@forEachIndexed
                        if (!PATCH_METHOD.containsKey(methodRef.name)) return@forEachIndexed
                        patchIndexes.addLast(index)
                    }
                    while (!patchIndexes.isEmpty())
                        patchAPI(context, classDef, method, patchIndexes.removeLast())
                }
            }
        }
        //Enable sim-spoof in settings
        with(SettingsStatusLoadFingerprint.result!!.mutableMethod) {
            addInstruction(
                0,
                "invoke-static {}, Lapp/revanced/tiktok/settingsmenu/SettingsStatus;->enableSimSpoof()V"
            )
        }
        return PatchResultSuccess()
    }

    //Patch android API and return fake sim information
    private fun patchAPI(context: BytecodeContext, classDef: ClassDef, method: Method, index: Int) {
        with(context.proxy(classDef).mutableClass.findMutableMethodOf(method)) {
            val methodRef = (instruction(index) as Instruction35c).reference as MethodReference
            val resultReg = (instruction(index + 1) as OneRegisterInstruction).registerA
            addInstructions(
                index + 2,
                """
                        invoke-static {v$resultReg}, Lapp/revanced/tiktok/simspoof/SimSpoof;->${PATCH_METHOD[methodRef.name]}(Ljava/lang/String;)Ljava/lang/String;
                        move-result-object v$resultReg
                    """
            )
        }
    }
}