package app.revanced.patches.music.misc.microg.patch.resource

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.implementation.ResourceData
import app.revanced.patcher.patch.implementation.ResourcePatch
import app.revanced.patcher.patch.implementation.misc.PatchResult
import app.revanced.patcher.patch.implementation.misc.PatchResultSuccess
import app.revanced.patches.music.misc.microg.annotations.MusicMicroGPatchCompatibility

@Name("music-microg-resource-patch")
@Description("Resource patch to allow YouTube Music ReVanced to run without root and under a different package name.")
@MusicMicroGPatchCompatibility
@Version("0.0.1")
class MusicMicroGResourcePatch  : ResourcePatch(){
    override fun execute(data: ResourceData): PatchResult {
        TODO("Not yet implemented")
        return PatchResultSuccess()
    }

}