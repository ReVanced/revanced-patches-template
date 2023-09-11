package app.revanced.patches.tudortmund.lockscreen.patch

import app.revanced.extensions.exception
import app.revanced.patcher.annotation.Compatibility
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Package
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.extensions.InstructionExtensions.replaceInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.patch.annotations.RequiresIntegrations
import app.revanced.patches.tudortmund.lockscreen.fingerprints.BrightnessFingerprint
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction22c
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction35c
import com.android.tools.smali.dexlib2.iface.reference.FieldReference
import com.android.tools.smali.dexlib2.iface.reference.MethodReference

@Patch
@Name("Show on lockscreen")
@Description("Shows student id and student ticket on lockscreen.")
@Compatibility([Package("de.tudortmund.app")])
@RequiresIntegrations
class ShowOnLockscreenPatch : BytecodePatch(
    listOf(BrightnessFingerprint)
) {
    override fun execute(context: BytecodeContext) {
        BrightnessFingerprint.result?.mutableMethod?.apply {
            val brightnessInstruction = implementation!!.instructions.firstNotNullOf { instruction ->
                if (instruction.opcode != Opcode.IGET_OBJECT) return@firstNotNullOf null

                val getInstruction = instruction as Instruction22c
                val fieldReference = getInstruction.reference as FieldReference

                if (fieldReference.type != "Ljava/lang/Float;") return@firstNotNullOf null

                instruction
            }

            val (windowIndex, activityRegister) = implementation!!.instructions.withIndex()
                .firstNotNullOf { (index, instruction) ->
                    if (instruction.opcode != Opcode.INVOKE_VIRTUAL)
                        return@firstNotNullOf null

                    val invokeInstruction = instruction as Instruction35c
                    val methodRef = invokeInstruction.reference as MethodReference

                    if (methodRef.name != "getWindow" || methodRef.returnType != "Landroid/view/Window;")
                        return@firstNotNullOf null

                    Pair(index, invokeInstruction.registerC)
                }

            val brightnessRegister = brightnessInstruction.registerA

            replaceInstruction(
                windowIndex,
                "invoke-static { v$activityRegister, v$brightnessRegister }, " +
                        "$INTEGRATIONS_CLASS_DESCRIPTOR->" +
                        "getWindow" +
                        "(Landroidx/appcompat/app/AppCompatActivity;F)" +
                        "Landroid/view/Window;"
            )

            addInstructions(
                windowIndex,
                """
                    invoke-virtual { v$brightnessRegister }, Ljava/lang/Float;->floatValue()F
                    move-result v$brightnessRegister
                """
            )

            addInstruction(windowIndex, brightnessInstruction)

        } ?: throw BrightnessFingerprint.exception
    }

    private companion object {
        const val INTEGRATIONS_CLASS_DESCRIPTOR = "Lapp/revanced/tudortmund/lockscreen/ShowOnLockscreenPatch;"
    }
}
