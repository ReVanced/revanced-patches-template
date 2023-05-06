package app.revanced.patches.youtube.layout.theme.bytecode.patch

import app.revanced.extensions.toErrorResult
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.data.toMethodWalker
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.extensions.instruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import app.revanced.patches.youtube.layout.theme.annotations.SeekbarColorCompatibility
import app.revanced.patches.youtube.layout.theme.bytecode.fingerprints.CreateDarkThemeSeekbarFingerprint
import app.revanced.patches.youtube.layout.theme.bytecode.fingerprints.SetSeekbarClickedColorFingerprint
import app.revanced.patches.youtube.layout.theme.patch.LithoColorHookPatch
import app.revanced.patches.youtube.layout.theme.patch.LithoColorHookPatch.Companion.lithoColorOverrideHook
import app.revanced.patches.youtube.layout.theme.resource.SeekbarColorResourcePatch
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
import app.revanced.util.patch.indexOfFirstConstantInstruction
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
            var registerIndex = indexOfFirstConstantInstruction(SeekbarColorResourcePatch.inlineTimeBarColorizedBarPlayedColorDarkId) + 2
            var colorRegister = (instruction(registerIndex) as OneRegisterInstruction).registerA
            addInstructions(
                registerIndex + 1,
                """
                        invoke-static { v$colorRegister }, $INTEGRATIONS_CLASS_DESCRIPTOR->getSeekbarColorValue(I)I
                        move-result v$colorRegister
                    """
            )
            
            registerIndex = indexOfFirstConstantInstruction(SeekbarColorResourcePatch.inlineTimeBarPlayedNotHighlightedColorId) + 2
            colorRegister = (instruction(registerIndex) as OneRegisterInstruction).registerA
            addInstructions(
                registerIndex + 1,
                """
                        invoke-static { v$colorRegister }, $INTEGRATIONS_CLASS_DESCRIPTOR->getSeekbarColorValue(I)I
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
                    val colorRegister = (method.instruction(0) as TwoRegisterInstruction).registerA
                    addInstructions(
                        0,
                        """
                            invoke-static { v$colorRegister }, $INTEGRATIONS_CLASS_DESCRIPTOR->getSeekbarColorOverride(I)I
                            move-result v$colorRegister
                        """
                    )
                }
            }
        } ?: return SetSeekbarClickedColorFingerprint.toErrorResult()

        lithoColorOverrideHook(INTEGRATIONS_CLASS_DESCRIPTOR, "getSeekbarColorOverride")

        return PatchResultSuccess()
    }

    private companion object {
        private const val INTEGRATIONS_CLASS_DESCRIPTOR = "Lapp/revanced/integrations/patches/theme/SeekbarColorPatch;"
    }
}
