package app.revanced.patches.youtube.layout.hidesubtitlespopup.patch

import app.revanced.extensions.toErrorResult
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.replaceInstructions
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.youtube.layout.hidesubtitlespopup.annotation.HideSubtitlesPopupPatchCompatibility
import app.revanced.patches.youtube.layout.hidesubtitlespopup.fingerprint.HideSubtitlesPopupFingerprint

@Patch
@Name("hide-subtitles-popup")
@Description("Hides the toast notification when toggling subtitles.")
@HideSubtitlesPopupPatchCompatibility
@Version("0.0.1")
class HideSubtitlesPopupPatch : BytecodePatch(
    listOf(HideSubtitlesPopupFingerprint)
) {
    override fun execute(context: BytecodeContext): PatchResult {
        HideSubtitlesPopupFingerprint.result?.mutableMethod?.replaceInstructions(0, "return-void")
            ?: return HideSubtitlesPopupFingerprint.toErrorResult()

        return PatchResultSuccess()
    }
}