package app.revanced.patches.youtube.misc.playercontrols

import app.revanced.extensions.exception
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint.Companion.resolve
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprintResult
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patches.shared.fingerprints.LayoutConstructorFingerprint
import app.revanced.patches.youtube.misc.playercontrols.fingerprints.BottomControlsInflateFingerprint
import app.revanced.patches.youtube.misc.playercontrols.fingerprints.PlayerControlsVisibilityFingerprint
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction

@Patch(
    description = "Manages the code for the player controls of the YouTube player.",
    dependencies = [BottomControlsResourcePatch::class],
)
object PlayerControlsBytecodePatch : BytecodePatch(
    setOf(LayoutConstructorFingerprint, BottomControlsInflateFingerprint)
) {
    lateinit var showPlayerControlsFingerprintResult: MethodFingerprintResult

    private var moveToRegisterInstructionIndex: Int = 0
    private var viewRegister: Int = 0

    override fun execute(context: BytecodeContext) {
        LayoutConstructorFingerprint.result?.let {
            if (!PlayerControlsVisibilityFingerprint.resolve(context, it.classDef))
                throw LayoutConstructorFingerprint.exception
        } ?: throw LayoutConstructorFingerprint.exception

        showPlayerControlsFingerprintResult = PlayerControlsVisibilityFingerprint.result!!
        inflateFingerprintResult = BottomControlsInflateFingerprint.result!!
    }

    private var inflateFingerprintResult: MethodFingerprintResult? = null
        set(fingerprint) {
            field = fingerprint!!.also {
                moveToRegisterInstructionIndex = it.scanResult.patternScanResult!!.endIndex
                viewRegister =
                    (it.mutableMethod.implementation!!.instructions[moveToRegisterInstructionIndex] as OneRegisterInstruction).registerA
            }
        }

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