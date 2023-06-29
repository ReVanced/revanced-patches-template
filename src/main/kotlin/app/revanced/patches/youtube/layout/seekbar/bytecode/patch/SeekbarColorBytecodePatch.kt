package app.revanced.patches.youtube.layout.seekbar.bytecode.patch

import app.revanced.extensions.indexOfFirstConstantInstructionValue
import app.revanced.extensions.toErrorResult
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.data.toMethodWalker
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import app.revanced.patches.youtube.layout.seekbar.annotations.SeekbarColorCompatibility
import app.revanced.patches.youtube.layout.seekbar.bytecode.fingerprints.CreateDarkThemeSeekbarFingerprint
import app.revanced.patches.youtube.layout.seekbar.bytecode.fingerprints.SetSeekbarClickedColorFingerprint
import app.revanced.patches.youtube.layout.seekbar.resource.SeekbarColorResourcePatch
import app.revanced.patches.youtube.layout.theme.bytecode.patch.LithoColorHookPatch
import app.revanced.patches.youtube.layout.theme.bytecode.patch.LithoColorHookPatch.Companion.lithoColorOverrideHook
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction
import org.jf.dexlib2.iface.instruction.TwoRegisterInstruction

@Description("Hide or set a custom seekbar color")
@DependsOn([IntegrationsPatch::class, LithoColorHookPatch::class, SeekbarColorResourcePatch::class])
@SeekbarColorCompatibility
@Version("0.0.1")
class SeekbarColorBytecodePatch : BytecodePatch(
    listOf(CreateDarkThemeSeekbarFingerprint, SetSeekbarClickedColorFingerprint)
) {
    override fun execute(context: BytecodeContext): PatchResult {
        CreateDarkThemeSeekbarFingerprint.result?.mutableMethod?.apply {
            var registerIndex = indexOfFirstConstantInstructionValue(SeekbarColorResourcePatch.inlineTimeBarColorizedBarPlayedColorDarkId) + 2
            var colorRegister = (getInstruction(registerIndex) as OneRegisterInstruction).registerA
            addInstructions(
                registerIndex + 1,
                """
                    invoke-static { v$colorRegister }, $INTEGRATIONS_CLASS_DESCRIPTOR->getVideoPlayerSeekbarColor(I)I
                    move-result v$colorRegister
                """
            )
            
            registerIndex = indexOfFirstConstantInstructionValue(SeekbarColorResourcePatch.inlineTimeBarPlayedNotHighlightedColorId) + 2
            colorRegister = (getInstruction(registerIndex) as OneRegisterInstruction).registerA
            addInstructions(
                registerIndex + 1,
                """
                    invoke-static { v$colorRegister }, $INTEGRATIONS_CLASS_DESCRIPTOR->getVideoPlayerSeekbarColor(I)I
                    move-result v$colorRegister
                """
            )
        } ?: return CreateDarkThemeSeekbarFingerprint.toErrorResult()

        SetSeekbarClickedColorFingerprint.result?.let { result ->
            result.mutableMethod.let {
                val setColorMethodIndex = result.scanResult.patternScanResult!!.startIndex + 1
                val method = context
                    .toMethodWalker(it)
                    .nextMethod(setColorMethodIndex, true)
                    .getMethod() as MutableMethod

                method.apply {
                    val colorRegister = (method.getInstruction(0) as TwoRegisterInstruction).registerA
                    addInstructions(
                        0,
                        """
                            invoke-static { v$colorRegister }, $INTEGRATIONS_CLASS_DESCRIPTOR->getVideoPlayerSeekbarClickedColor(I)I
                            move-result v$colorRegister
                        """
                    )
                }
            }
        } ?: return SetSeekbarClickedColorFingerprint.toErrorResult()

        lithoColorOverrideHook(INTEGRATIONS_CLASS_DESCRIPTOR, "getLithoColor")

        return PatchResultSuccess()
    }

    private companion object {
        private const val INTEGRATIONS_CLASS_DESCRIPTOR = "Lapp/revanced/integrations/patches/theme/SeekbarColorPatch;"
    }
}
