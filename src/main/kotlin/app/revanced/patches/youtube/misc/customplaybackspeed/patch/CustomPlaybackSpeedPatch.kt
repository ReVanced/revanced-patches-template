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
      
        if(arrayGenMethod.implementation!!.instructions[11].opcode != Opcode.MOVE_RESULT)
            return PatchResultError("Could't find correct instructions")

        arrayGenMethod.implementation!!.replaceInstruction(11, 
            "const/4 v0, 0x0".toInstruction()
        )    

        arrayGenMethod.addInstructions(28, 
            """
            sget-object v4, Lfi/razerman/youtube/XGlobals;->videoSpeeds:[F
            array-length v0, v4
            """
        )
        
        arrayGenMethod.implementation!!.replaceInstruction(35,
            "sget-object v4, Lfi/razerman/youtube/XGlobals;->videoSpeeds:[F".toInstruction()
        )

        val limiterMethodImpl = SpeedLimiterSignature.result?.method!!.implementation!!
        
        val speedLimitMin = 0.25f 
        val speedLimitMax = 100f

        fun hexFloat(float: Float): String = "0x%08x".format(float.toRawBits()) 

        limiterMethodImpl.replaceInstruction(5, 
            "const/high16 v0, ${hexFloat(speedLimitMin)}".toInstruction()
        )
        limiterMethodImpl.replaceInstruction(6, 
            "const/high16 v1, ${hexFloat(speedLimitMax)}".toInstruction()
        )

        return PatchResultSuccess()
    }
}
