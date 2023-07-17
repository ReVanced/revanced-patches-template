package app.revanced.patches.music.layout.minimizedplayback.patch

import app.revanced.patcher.BytecodeContext
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.music.annotations.MusicCompatibility
import app.revanced.patches.music.layout.minimizedplayback.fingerprints.MinimizedPlaybackManagerFingerprint

@Patch
@Name("Minimized playback music")
@Description("Enables minimized playback on Kids music.")
@MusicCompatibility
@Version("0.0.1")
class MinimizedPlaybackPatch : BytecodePatch(
    listOf(MinimizedPlaybackManagerFingerprint)
) {
    override suspend fun execute(context: BytecodeContext) {
        MinimizedPlaybackManagerFingerprint.result!!.mutableMethod.addInstruction(
            0,
            """
                return-void
            """
        )

    }
}
