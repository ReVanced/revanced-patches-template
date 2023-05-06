package app.revanced.patches.youtube.layout.theme.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.youtube.layout.theme.bytecode.fingerprints.CreateDarkThemeSeekbarFingerprint
import app.revanced.patches.youtube.layout.theme.bytecode.fingerprints.SetSeekbarClickedColorFingerprint
import app.revanced.patches.youtube.layout.theme.bytecode.patch.SeekbarColorBytecodePatch

@Patch
@Name("theme")
@Description("Applies a custom theme.")
@DependsOn([LithoColorHookPatch::class, SeekbarColorBytecodePatch::class, ThemeResourcePatch::class])
@Version("0.0.1")
class ThemeBytecodePatch : BytecodePatch(
    listOf(CreateDarkThemeSeekbarFingerprint, SetSeekbarClickedColorFingerprint)
) {

    override fun execute(context: BytecodeContext): PatchResult {
        LithoColorHookPatch.lithoColorOverrideHook(INTEGRATIONS_CLASS_DESCRIPTOR, "getValue")

        return PatchResultSuccess()
    }

    companion object {
        private const val INTEGRATIONS_CLASS_DESCRIPTOR = "Lapp/revanced/integrations/patches/theme/ThemeLithoComponentsPatch;"
    }
}
