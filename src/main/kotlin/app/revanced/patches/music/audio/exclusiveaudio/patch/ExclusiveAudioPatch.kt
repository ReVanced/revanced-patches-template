package app.revanced.patches.music.audio.exclusiveaudio.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.impl.BytecodeData
import app.revanced.patcher.extensions.addInstruction
import app.revanced.patcher.extensions.replaceInstruction
import app.revanced.patcher.fingerprint.method.utils.MethodFingerprintUtils.resolve
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.patch.impl.BytecodePatch
import app.revanced.patches.music.audio.exclusiveaudio.annotations.ExclusiveAudioCompatibility
import app.revanced.patches.music.audio.exclusiveaudio.fingerprints.AudioOnlyEnablerFingerprint
import app.revanced.patches.music.audio.exclusiveaudio.fingerprints.ExclusiveAudioFingerprint

@Patch
@Name("exclusive-audio-playback")
@Description("Adds an option to play music without video.")
@ExclusiveAudioCompatibility
@Version("0.0.1")
class ExclusiveAudioPatch : BytecodePatch(
    listOf(
        AudioOnlyEnablerFingerprint
    )
) {
    override fun execute(data: BytecodeData): PatchResult {
        val method = AudioOnlyEnablerFingerprint.result!!.mutableMethod
        method.replaceInstruction(method.implementation!!.instructions.count() - 1, "const/4 v0, 0x1")
        method.addInstruction("return v0")

        return PatchResultSuccess()
    }
}
