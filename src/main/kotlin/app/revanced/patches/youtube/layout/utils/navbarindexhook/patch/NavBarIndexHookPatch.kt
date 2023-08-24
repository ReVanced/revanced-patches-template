package app.revanced.patches.youtube.layout.utils.navbarindexhook.patch

import app.revanced.extensions.toErrorResult
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patches.shared.fingerprints.OnBackPressedFingerprint
import app.revanced.patches.youtube.layout.utils.navbarindexhook.fingerprints.NavBarBuilderFingerprint
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction35c
import com.android.tools.smali.dexlib2.iface.reference.MethodReference

@Name("Hook NavBar index")
class NavBarIndexHookPatch : BytecodePatch(
    listOf(
        NavBarBuilderFingerprint,
        OnBackPressedFingerprint
    )
) {
    override fun execute(context: BytecodeContext) {
        /**
         *  Reset the NavBar index value when exiting the WatchWhileActivity
         */
        OnBackPressedFingerprint.result?.let {
            it.mutableMethod.apply {
                addInstruction(
                    0,
                    "invoke-static {}, $INTEGRATIONS_CLASS_DESCRIPTOR->setLastNavBarIndex()V"
                )
            }
        } ?: throw OnBackPressedFingerprint.toErrorResult()

        /**
         * Change NavBar Index value according to selected Tab
         */
        NavBarBuilderFingerprint.result?.let {
            val endIndex = it.scanResult.patternScanResult!!.endIndex
            val onClickListener =
                it.mutableMethod.getInstruction<ReferenceInstruction>(endIndex).reference.toString()

            val targetMethod =
                context.findClass(onClickListener)?.mutableClass?.methods?.first { method -> method.name == "onClick" }

            targetMethod?.apply {
                for ((index, instruction) in implementation!!.instructions.withIndex()) {
                    if (instruction.opcode != Opcode.INVOKE_VIRTUAL) continue

                    val invokeInstruction = instruction as Instruction35c
                    if ((invokeInstruction.reference as MethodReference).name != "indexOf") continue

                    val targetIndex = index + 2
                    if (getInstruction(targetIndex).opcode != Opcode.INVOKE_VIRTUAL) continue

                    val targetRegister = getInstruction<OneRegisterInstruction>(index + 1).registerA

                    addInstruction(
                        targetIndex,
                        "invoke-static {v$targetRegister}, $INTEGRATIONS_CLASS_DESCRIPTOR->setCurrentNavBarIndex(I)V"
                    )
                    break
                }
            }
        } ?: throw NavBarBuilderFingerprint.toErrorResult()

        /**
         * Initialize NavBar Index
         */

        context.initializeIndex(INTEGRATIONS_CLASS_DESCRIPTOR, "initializeIndex")
    }

    private companion object {
        const val INTEGRATIONS_CLASS_DESCRIPTOR =
            "Lapp/revanced/integrations/utils/NavBarIndexHook;"

        fun BytecodeContext.initializeIndex(
            classDescriptor: String,
            methodDescriptor: String
        ) {
            this.classes.forEach { classDef ->
                classDef.methods.forEach { method ->
                    if (classDef.type.endsWith("/WatchWhileActivity;") && method.name == "onCreate") {
                        val hookMethod =
                            this.proxy(classDef).mutableClass.methods.first { it.name == "onCreate" }

                        hookMethod.addInstruction(
                            2,
                            "invoke-static/range {p0 .. p0}, $classDescriptor->$methodDescriptor(Landroid/content/Context;)V"
                        )
                    }
                }
            }
        }
    }
}