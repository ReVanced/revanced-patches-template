package app.revanced.patches.youtube.misc.video.speed.current.patch

import app.revanced.extensions.toErrorResult
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.addInstruction
import app.revanced.patcher.extensions.instruction
import app.revanced.patcher.patch.*
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch
import app.revanced.patches.youtube.misc.video.speed.current.annotation.CurrentPlaybackSpeedCompatibility
import app.revanced.patches.youtube.misc.video.speed.current.fingerprint.OnPlaybackSpeedItemClickFingerprint
import app.revanced.patches.youtube.misc.video.speed.remember.patch.RememberPlaybackSpeedPatch
import app.revanced.patches.youtube.misc.video.videoid.patch.VideoIdPatch
import org.jf.dexlib2.Opcode
import org.jf.dexlib2.builder.BuilderInstruction
import org.jf.dexlib2.iface.instruction.FiveRegisterInstruction
import org.jf.dexlib2.iface.instruction.Instruction
import org.jf.dexlib2.iface.instruction.ReferenceInstruction

@Name("current-playback-speed")
@Description("Hook to get the current video playback speed")
@DependsOn([IntegrationsPatch::class, SettingsPatch::class, VideoIdPatch::class])
@CurrentPlaybackSpeedCompatibility
@Version("0.0.1")
class CurrentPlaybackSpeedPatch : BytecodePatch(
    listOf(
        OnPlaybackSpeedItemClickFingerprint
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {
        VideoIdPatch.injectCall("$INTEGRATIONS_CLASS_DESCRIPTOR->newVideoLoaded(Ljava/lang/String;)V")

        // User selected a playback speed
        OnPlaybackSpeedItemClickFingerprint.result?.apply {
            speedSelectionInsertMethod = mutableMethod
            val speedSelectionMethodInstructions = mutableMethod.implementation!!.instructions
            speedSelectionInsertIndex = scanResult.patternScanResult!!.startIndex - 3
            speedSelectionValueRegister =
                (mutableMethod.instruction(speedSelectionInsertIndex) as FiveRegisterInstruction).registerD

            setPlaybackSpeedContainerClassFieldReference =
                getReference(speedSelectionMethodInstructions, -1, Opcode.IF_EQZ)
            setPlaybackSpeedClassFieldReference =
                getReference(speedSelectionMethodInstructions, 1, Opcode.IGET)
            setPlaybackSpeedMethodReference =
                getReference(speedSelectionMethodInstructions, 2, Opcode.IGET)
        } ?: return OnPlaybackSpeedItemClickFingerprint.toErrorResult()


        injectVideoSpeedSelectedByUser("$INTEGRATIONS_CLASS_DESCRIPTOR->userSelectedPlaybackSpeed(F)V")

        return PatchResultSuccess()
    }

    companion object {
        private const val INTEGRATIONS_CLASS_DESCRIPTOR =
            "Lapp/revanced/integrations/patches/playback/speed/CurrentPlaybackSpeedPatch;"

        private lateinit var speedSelectionInsertMethod: MutableMethod
        private var speedSelectionInsertIndex = 0
        private var speedSelectionValueRegister = 0

        /**
         * Used by [RememberPlaybackSpeedPatch]
         */
        lateinit var setPlaybackSpeedContainerClassFieldReference: String
        lateinit var setPlaybackSpeedClassFieldReference: String
        lateinit var setPlaybackSpeedMethodReference: String

        private fun getReference(instructions: List<BuilderInstruction>, offset: Int, opcode: Opcode) =
            instructions[instructions.indexOfFirst { it.opcode == opcode } + offset].reference

        fun injectVideoSpeedSelectedByUser(
            methodDescriptor: String
        ) = speedSelectionInsertMethod.addInstruction(
            speedSelectionInsertIndex++,
            "invoke-static {v$speedSelectionValueRegister}, $methodDescriptor"
        )

        val Instruction.reference get() = (this as ReferenceInstruction).reference.toString()
    }
}