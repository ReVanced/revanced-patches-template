package app.revanced.patches.youtube.misc.playercontrols.bytecode.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.impl.BytecodeData
import app.revanced.patcher.extensions.addInstruction
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprintResult
import app.revanced.patcher.fingerprint.method.utils.MethodFingerprintUtils.resolve
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.Dependencies
import app.revanced.patcher.patch.impl.BytecodePatch
import app.revanced.patches.youtube.interaction.downloads.annotation.DownloadsCompatibility
import app.revanced.patches.youtube.misc.mapping.patch.ResourceIdMappingProviderResourcePatch
import app.revanced.patches.youtube.misc.playercontrols.fingerprints.BottomControlsInflateFingerprint
import app.revanced.patches.youtube.misc.playercontrols.fingerprints.PlayerControlsVisibilityFingerprint
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction

@Name("player-controls-bytecode-patch")
@Dependencies([ResourceIdMappingProviderResourcePatch::class])
@Description("Manages the code for the player controls of the YouTube player.")
@DownloadsCompatibility
@Version("0.0.1")
class PlayerControlsBytecodePatch : BytecodePatch(
    listOf(PlayerControlsVisibilityFingerprint)
) {
    override fun execute(data: BytecodeData): PatchResult {
        showPlayerControlsFingerprintResult = PlayerControlsVisibilityFingerprint.result!!

        bottomUiContainerResourceId = ResourceIdMappingProviderResourcePatch
            .resourceMappings
            .single { it.type == "id" && it.name == "bottom_ui_container_stub" }.id

        // TODO: another solution is required, this is hacky
        listOf(BottomControlsInflateFingerprint).resolve(data, data.classes)
        inflateFingerprintResult = BottomControlsInflateFingerprint.result!!

        return PatchResultSuccess()
    }

    internal companion object {
        var bottomUiContainerResourceId: Long = 0

        lateinit var showPlayerControlsFingerprintResult: MethodFingerprintResult

        private var inflateFingerprintResult: MethodFingerprintResult? = null
            set(fingerprint) {
                field = fingerprint!!.also {
                    moveToRegisterInstructionIndex = it.patternScanResult!!.endIndex
                    viewRegister =
                        (it.mutableMethod.implementation!!.instructions[moveToRegisterInstructionIndex] as OneRegisterInstruction).registerA
                }
            }

        private var moveToRegisterInstructionIndex: Int = 0
        private var viewRegister: Int = 0

        /**
         * Injects the code to change the visibility of controls.
         * @param descriptor The descriptor of the method which should be called.
         */
        fun injectVisibilityCheckCall(descriptor: String) {
            showPlayerControlsFingerprintResult.mutableMethod.addInstruction(
                0,
                """
                    invoke-static {p1}, $descriptor
                """
            )
        }

        /**
         * Injects the code to initialize the controls.
         * @param descriptor The descriptor of the method which should be calleed.
         */
        fun initializeControl(descriptor: String) {
            inflateFingerprintResult!!.mutableMethod.addInstruction(
                moveToRegisterInstructionIndex + 1,
                "invoke-static {v$viewRegister}, $descriptor"
            )
        }
    }
}