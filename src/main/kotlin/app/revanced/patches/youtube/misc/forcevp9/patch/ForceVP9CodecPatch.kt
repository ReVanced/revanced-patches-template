package app.revanced.patches.youtube.misc.forcevp9.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.impl.BytecodeData
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultError
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.patch.impl.BytecodePatch
import app.revanced.patches.youtube.misc.forcevp9.annotations.ForceVP9Compatibility
import app.revanced.patches.youtube.misc.forcevp9.fingerprints.ForceVP9ParentFingerprint

@Patch
@Name("force-vp9-codec")
@Description("Forces the VP9 codec for videos.")
@ForceVP9Compatibility
@Version("0.0.1")
class ForceVP9CodecPatch : BytecodePatch(
    listOf(
        ForceVP9ParentFingerprint
    )
) {
    override fun execute(data: BytecodeData): PatchResult {
        return PatchResultError("Error: Not yet supported!")
    }
}
