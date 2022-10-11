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
import app.revanced.patches.youtube.layout.theme.annotations.ThemeCompatibility
import app.revanced.patches.youtube.layout.theme.fingerprints.CommentsFilterBarFingerprint

@Name("comment-filter-bar-theme")
@Description("Apply theme to comments filter action bar.")
@ThemeCompatibility
@Version("0.0.1")
class CommentsFilterBarPatch : BytecodePatch(
    listOf(
        CommentsFilterBarFingerprint
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {

        val result = CommentsFilterBarFingerprint.result!!
        val method = CommentsFilterBarFingerprint.result!!.mutableMethod
        val patchIndex = result.scanResult.patternScanResult!!.endIndex - 1

        method.addInstructions(
            patchIndex, """
                const v1, -0xdededf
                if-ne v1, p1, :comments_action_bar
                const/4 p1, 0x0
            """, listOf(ExternalLabel("comments_action_bar", method.instruction(patchIndex)))
        )
        return PatchResultSuccess()
    }
}