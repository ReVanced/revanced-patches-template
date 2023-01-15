package app.revanced.patches.youtube.layout.hidesubtitlespopup.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.replaceInstructions
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patches.youtube.layout.hidesubtitlespopup.annotation.HideSubtitlesPopupPatchCompatibility
import app.revanced.patches.youtube.layout.hidesubtitlespopup.fingerprint.HideSubtitlesPopupFingerprint

@Patch
@DependsOn()
@Name("hide-subtitles-popup")
@Description("Hides subtitles popup.")
@HideSubtitlesPopupPatchCompatibility
@Version("0.0.1")
class HideSubtitlesPopupPatch : BytecodePatch(
    listOf(
        HideSubtitlesPopupFingerprint
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {

        val subtitlePopupMethod = HideSubtitlesPopupFingerprint.result!!.mutableMethod

        // add a return instruction at the start of the void method
        subtitlePopupMethod.replaceInstructions(
            0,
            "return-void"
        )

        return PatchResultSuccess()
    }

}