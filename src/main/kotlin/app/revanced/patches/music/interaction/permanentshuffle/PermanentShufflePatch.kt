package app.revanced.patches.music.interaction.permanentshuffle

import app.revanced.extensions.exception
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patches.music.interaction.permanentshuffle.fingerprints.DisableShuffleFingerprint


@Patch(
    name = "Permanent shuffle",
    description = "Permanently remember your shuffle preference " +
            "even if the playlist ends or another track is played.",
    compatiblePackages = [CompatiblePackage("com.google.android.apps.youtube.music")],
    use = false
)
@Suppress("unused")
object PermanentShuffleTogglePatch : BytecodePatch(setOf(DisableShuffleFingerprint)) {
    override fun execute(context: BytecodeContext) {
        DisableShuffleFingerprint.result?.mutableMethod?.addInstruction(0, "return-void")
            ?: throw DisableShuffleFingerprint.exception
    }
}
