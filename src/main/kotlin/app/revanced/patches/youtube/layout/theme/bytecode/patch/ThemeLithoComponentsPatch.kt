package app.revanced.patches.youtube.layout.theme.bytecode.patch

import app.revanced.extensions.error
import app.revanced.patcher.BytecodeContext
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patches.youtube.layout.theme.annotations.ThemeCompatibility
import app.revanced.patches.youtube.layout.theme.fingerprints.LithoThemeFingerprint

@Name("theme-litho-components")
@Description("Applies a custom theme to Litho components.")
@ThemeCompatibility
@Version("0.0.1")
class ThemeLithoComponentsPatch : BytecodePatch(listOf(LithoThemeFingerprint)) {
    override fun execute(context: BytecodeContext) {
        LithoThemeFingerprint.result?.let {
            it.mutableMethod.apply {
                val patchIndex = it.scanResult.patternScanResult!!.endIndex - 1

                addInstructions(
                    patchIndex,
                    """
                        invoke-static {p1}, $INTEGRATIONS_CLASS_DESCRIPTOR->getValue(I)I
                        move-result p1
                    """
                )
            }
        } ?: LithoThemeFingerprint.error()
    }

    private companion object {
        private const val INTEGRATIONS_CLASS_DESCRIPTOR = "Lapp/revanced/integrations/patches/theme/ThemeLithoComponentsPatch;"
    }
}