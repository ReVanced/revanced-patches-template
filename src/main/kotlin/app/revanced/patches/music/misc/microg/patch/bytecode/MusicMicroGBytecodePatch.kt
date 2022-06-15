package app.revanced.patches.music.misc.microg.patch.bytecode

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.implementation.BytecodeData
import app.revanced.patcher.patch.annotations.Dependencies
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.patch.implementation.BytecodePatch
import app.revanced.patcher.patch.implementation.misc.PatchResult
import app.revanced.patcher.patch.implementation.misc.PatchResultSuccess
import app.revanced.patches.music.misc.microg.annotations.MusicMicroGPatchCompatibility
import app.revanced.patches.youtube.misc.microg.patch.resource.MicroGResourcePatch

@Patch(include = false)
@Dependencies(dependencies = [MicroGResourcePatch::class])
@Name("music-microg-support")
@Description("Patch to allow YouTube Music ReVanced to run without root and under a different package name.")
@MusicMicroGPatchCompatibility
@Version("0.0.1")
class MusicMicroGBytecodePatch : BytecodePatch(
    listOf()
) {
    override fun execute(data: BytecodeData): PatchResult {
        TODO("Not yet implemented")
        return PatchResultSuccess()
    }
}