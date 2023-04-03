package app.revanced.patches.youtube.misc.video.speed.current.patch

import app.revanced.extensions.toErrorResult
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.addInstruction
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.extensions.instruction
import app.revanced.patcher.patch.*
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import app.revanced.patcher.util.smali.ExternalLabel
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch
import app.revanced.patches.youtube.misc.video.speed.current.annotation.CurrentPlaybackSpeedCompatibility
import app.revanced.patches.youtube.misc.video.speed.current.fingerprint.InitializePlaybackSpeedValuesFingerprint
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
        OnPlaybackSpeedItemClickFingerprint,
        InitializePlaybackSpeedValuesFingerprint
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

            speedOverrideSetPlaybackSpeedContainerClassFieldReference =
                getReference(speedSelectionMethodInstructions, -1, Opcode.IF_EQZ)
            speedOverrideSetPlaybackSpeedClassFieldReference =
                getReference(speedSelectionMethodInstructions, 1, Opcode.IGET)
            speedOverrideSetPlaybackSpeedMethodReference =
                getReference(speedSelectionMethodInstructions, 2, Opcode.IGET)
        } ?: return OnPlaybackSpeedItemClickFingerprint.toErrorResult()

        /*
         * Hook the code that is called when the playback speeds are initialized, and sets the playback speed
         */
        InitializePlaybackSpeedValuesFingerprint.result?.apply {
            speedOverrideMethod = mutableMethod
            speedOverrideOnItemClickListenerClassFieldReference = mutableMethod.instruction(0).reference
        } ?: return InitializePlaybackSpeedValuesFingerprint.toErrorResult()

        injectVideoSpeedSelectedByUser("$INTEGRATIONS_CLASS_DESCRIPTOR->userSelectedPlaybackSpeed(F)V")

        return PatchResultSuccess()
    }

    companion object {
        private const val INTEGRATIONS_CLASS_DESCRIPTOR =
            "Lapp/revanced/integrations/patches/playback/speed/CurrentPlaybackSpeedPatch;"

        private lateinit var speedSelectionInsertMethod: MutableMethod
        private var speedSelectionInsertIndex = 0
        private var speedSelectionValueRegister = 0

        private fun getReference(instructions: List<BuilderInstruction>, offset: Int, opcode: Opcode) =
            instructions[instructions.indexOfFirst { it.opcode == opcode } + offset].reference

        fun injectVideoSpeedSelectedByUser(
            methodDescriptor: String
        ) = speedSelectionInsertMethod.addInstruction(
            speedSelectionInsertIndex++,
            "invoke-static {v$speedSelectionValueRegister}, $methodDescriptor"
        )

        private val Instruction.reference get() = (this as ReferenceInstruction).reference.toString()

        // everything necessary for calling the method setPlaybackSpeed()
        private lateinit var speedOverrideMethod: MutableMethod
        private lateinit var speedOverrideOnItemClickListenerClassFieldReference: String
        private lateinit var speedOverrideSetPlaybackSpeedContainerClassFieldReference: String
        private lateinit var speedOverrideSetPlaybackSpeedClassFieldReference: String
        private lateinit var speedOverrideSetPlaybackSpeedMethodReference: String

        /**
         * Used only for [RememberPlaybackSpeedPatch]
         */
        fun injectVideoSpeedOverride(
            methodDescriptor: String
        ) = speedOverrideMethod.addInstructions(
            // Registers are not used at index 0, so they can be freely used.
            0,
            """
                invoke-static { }, $methodDescriptor
                move-result v0
                
                # Check if the playback speed is not 1.0x.
                const/high16 v1, 0x3f800000  # 1.0f
                cmpg-float v1, v0, v1
                if-eqz v1, :do_not_override

                # Get the instance of the class which has the container class field below.
                iget-object v1, p0, $speedOverrideOnItemClickListenerClassFieldReference

                # Get the container class field.
                iget-object v1, v1, $speedOverrideSetPlaybackSpeedContainerClassFieldReference 
                
                # Get the field from its class.
                iget-object v2, v1, $speedOverrideSetPlaybackSpeedClassFieldReference
                
                # Invoke setPlaybackSpeed on that class.
                invoke-virtual {v2, v0}, $speedOverrideSetPlaybackSpeedMethodReference
            """.trimIndent(),
            listOf(ExternalLabel("do_not_override", speedOverrideMethod.instruction(0)))
        )
    }
}