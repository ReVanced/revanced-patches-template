package app.revanced.patches.youtube.layout.theme.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.extensions.instruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.util.smali.ExternalLabel
import app.revanced.patches.youtube.layout.autocaptions.fingerprints.StartVideoInformerFingerprint
import app.revanced.patches.youtube.layout.sponsorblock.bytecode.fingerprints.ShortsPlayerConstructorFingerprint
import app.revanced.patches.youtube.layout.theme.annotations.ThemeCompatibility
import app.revanced.patches.youtube.layout.theme.fingerprints.LithoThemeFingerprint

@Name("litho-components-theme")
@Description("Applies a custom theme to litho components.")
@ThemeCompatibility
@Version("0.0.1")
class LithoThemePatch : BytecodePatch(
    listOf(
        LithoThemeFingerprint
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {
        val result = LithoThemeFingerprint.result!!
        val method = result.mutableMethod
        val patchIndex = result.scanResult.patternScanResult!!.endIndex - 1

        method.addInstructions(
            patchIndex, """
                invoke-static {p1}, Lapp/revanced/integrations/patches/LithoThemePatch;->applyLithoTheme(I)I
                move-result p1
            """
        )

        fun buildInvokeString(player: Int) = """
            const/4 v0, 0x$player
            sput-boolean v0, Lapp/revanced/integrations/patches/LithoThemePatch;->isStandardPlayer:Z
        """

        StartVideoInformerFingerprint.result!!.mutableMethod.addInstructions(
            0, buildInvokeString(1)
        )
        ShortsPlayerConstructorFingerprint.result!!.mutableMethod.addInstructions(
            0, buildInvokeString(0)
        )

        return PatchResultSuccess()
    }
}
