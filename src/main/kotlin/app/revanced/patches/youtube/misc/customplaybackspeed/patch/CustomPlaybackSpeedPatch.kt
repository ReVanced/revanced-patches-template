package app.revanced.patches.youtube.misc.customplaybackspeed.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.implementation.BytecodeData
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.util.smali.toInstruction
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.patch.implementation.BytecodePatch
import app.revanced.patcher.patch.implementation.misc.PatchResult
import app.revanced.patcher.patch.implementation.misc.PatchResultSuccess
import app.revanced.patcher.patch.implementation.misc.PatchResultError
import app.revanced.patches.youtube.misc.customplaybackspeed.annotations.CustomPlaybackSpeedCompatibility
import app.revanced.patches.youtube.misc.customplaybackspeed.signatures.ArrayGeneratorSignature
import app.revanced.patches.youtube.misc.customplaybackspeed.signatures.SpeedLimiterSignature
import app.revanced.patcher.util.proxy.mutableTypes.encodedValue.MutableArrayEncodedValue
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
import app.revanced.patcher.patch.annotations.Dependencies
import org.jf.dexlib2.builder.BuilderInstruction
import org.jf.dexlib2.Opcode
import org.jf.dexlib2.iface.instruction.ReferenceInstruction
import org.jf.dexlib2.iface.instruction.formats.Instruction35c
import org.jf.dexlib2.iface.instruction.formats.Instruction11x
import org.jf.dexlib2.iface.instruction.formats.Instruction11n
import org.jf.dexlib2.iface.instruction.formats.Instruction21c
import org.jf.dexlib2.iface.instruction.formats.Instruction21ih
import org.jf.dexlib2.iface.reference.MethodReference
import org.jf.dexlib2.iface.reference.FieldReference

@Patch
@Name("custom-playback-speed")
@Description("Allows to change the default playback speed options")
@Dependencies(dependencies = [IntegrationsPatch::class])
@CustomPlaybackSpeedCompatibility
@Version("0.0.1")
class CustomPlaybackSpeedPatch : BytecodePatch(listOf(
    ArrayGeneratorSignature, SpeedLimiterSignature
    )) {

    override fun execute(data: BytecodeData): PatchResult {
        val arrayGenMethod = ArrayGeneratorSignature.result?.method!!
        val arrayGenMethodImpl = arrayGenMethod.implementation!!

        val sizeCallIndex = arrayGenMethodImpl.instructions
            .indexOfFirst { ((it as? ReferenceInstruction)?.reference as? MethodReference)?.name == "size" }

        if(sizeCallIndex == -1) return PatchResultError("Couldn't find call to size()")

        val sizeCallResultRegister = (arrayGenMethodImpl.instructions[sizeCallIndex + 1] as Instruction11x).registerA

        arrayGenMethodImpl.replaceInstruction(sizeCallIndex + 1, 
            "const/4 v$sizeCallResultRegister, 0x0".toInstruction()
        )    

        val (arrayLengthConstIndex, arrayLengthConst) = arrayGenMethodImpl.instructions.withIndex()
            .first {(it.value as? Instruction11n)?.narrowLiteral == 7 }

        val arrayLengthConstDestination = (arrayLengthConst as Instruction11n).registerA

        val videoSpeedsArrayType = "Lfi/razerman/youtube/XGlobals;->videoSpeeds:[F"

        arrayGenMethod.addInstructions(arrayLengthConstIndex + 1, 
            """
            sget-object v$arrayLengthConstDestination, $videoSpeedsArrayType
            array-length v$arrayLengthConstDestination, v$arrayLengthConstDestination
            """
        )
        
        val (originalArrayFetchIndex, originalArrayFetch) = arrayGenMethodImpl.instructions.withIndex()
            .first { 
                ((it.value as? ReferenceInstruction)?.reference as? FieldReference)?.definingClass?.contains("PlayerConfigModel") ?: false &&
                ((it.value as? ReferenceInstruction)?.reference as? FieldReference)?.type == "[F"
            }

        val originalArrayFetchDestination = (originalArrayFetch as Instruction21c).registerA

        arrayGenMethodImpl.replaceInstruction(originalArrayFetchIndex,
            "sget-object v$originalArrayFetchDestination, $videoSpeedsArrayType".toInstruction()
        )

        val limiterMethodImpl = SpeedLimiterSignature.result?.method!!.implementation!!
        
        val speedLimitMin = 0.25f 
        val speedLimitMax = 100f

        fun hexFloat(float: Float): String = "0x%08x".format(float.toRawBits()) 

        val (limiterMinConstIndex, limiterMinConst) = limiterMethodImpl.instructions.withIndex()
            .first { (it.value as? Instruction21ih)?.narrowLiteral == 0.25f.toRawBits() }        
        val (limiterMaxConstIndex, limiterMaxConst) = limiterMethodImpl.instructions.withIndex()
            .first { (it.value as? Instruction21ih)?.narrowLiteral == 2.0f.toRawBits() }

        val limiterMinConstDestination = (limiterMinConst as Instruction21ih).registerA
        val limiterMaxConstDestination = (limiterMaxConst as Instruction21ih).registerA

        limiterMethodImpl.replaceInstruction(limiterMinConstIndex, 
            "const/high16 v$limiterMinConstDestination, ${hexFloat(speedLimitMin)}".toInstruction()
        )
        limiterMethodImpl.replaceInstruction(limiterMaxConstIndex, 
            "const/high16 v$limiterMaxConstDestination, ${hexFloat(speedLimitMax)}".toInstruction()
        )

        return PatchResultSuccess()
    }
}
