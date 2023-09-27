package app.revanced.patches.youtube.video.playerresponse

import app.revanced.extensions.exception
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import app.revanced.patches.youtube.misc.integrations.IntegrationsPatch
import app.revanced.patches.youtube.video.playerresponse.fingerprint.PlayerParameterBuilderFingerprint

@Patch(
    dependencies = [IntegrationsPatch::class],
)
object PlayerResponseMethodHookPatch : BytecodePatch(
    setOf(PlayerParameterBuilderFingerprint)
) {
    private const val VIDEO_ID_PARAMETER = 1
    private const val PROTO_BUFFER_PARAMETER_PARAMETER = 3

    private lateinit var playerResponseMethod: MutableMethod

    // Hook insert indices are incremented in the order they're added.
    private var videoIdHookInsertIndex = 0
    private var protoBufferParameterHookInsertIndex = 0

    override fun execute(context: BytecodeContext) {
        playerResponseMethod = PlayerParameterBuilderFingerprint.result?.mutableMethod
            ?: throw PlayerParameterBuilderFingerprint.exception
    }

    fun hookVideoId(methodDescriptor: String) {
        playerResponseMethod.addInstruction(
            ++videoIdHookInsertIndex, "invoke-static {p$VIDEO_ID_PARAMETER}, $methodDescriptor"
        )

        // TODO: Explain why any video id hook has to precede any protoBufferParameterHook.
        protoBufferParameterHookInsertIndex++
    }

    fun hookProtoBufferParameter(methodDescriptor: String) {
        playerResponseMethod.addInstructions(
            protoBufferParameterHookInsertIndex,
            """
               invoke-static {p$PROTO_BUFFER_PARAMETER_PARAMETER}, $methodDescriptor
               move-result-object p$PROTO_BUFFER_PARAMETER_PARAMETER
            """
        )

        protoBufferParameterHookInsertIndex += 2
    }
}

