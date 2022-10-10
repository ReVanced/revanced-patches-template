package app.revanced.patches.youtube.layout.theme.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patches.youtube.layout.theme.annotations.ThemeCompatibility
import app.revanced.patches.youtube.layout.theme.fingerprints.CommentActionbarFingerprint

@Name("comment-actionbar-theme")
@Description("Apply theme to comment action bar.")
@ThemeCompatibility
@Version("0.0.1")
class CommentActionbarPatch : BytecodePatch(
    listOf(
        CommentActionbarFingerprint
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {

        val result = CommentActionbarFingerprint.result!!

        result.mutableMethod.addInstructions(
            result.scanResult.patternScanResult!!.endIndex - 1, """
            const v1, -0xdededf
            if-ne v1, p1, :isdark
            const/4 p1, 0x0
            :isdark
            nop
        """
        )
        return PatchResultSuccess()
    }
}