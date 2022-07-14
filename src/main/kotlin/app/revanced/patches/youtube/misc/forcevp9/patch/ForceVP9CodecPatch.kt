package app.revanced.patches.youtube.misc.forcevp9.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.impl.BytecodeData
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.extensions.removeInstructions
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprintResult
import app.revanced.patcher.fingerprint.method.utils.MethodFingerprintUtils.resolve
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.patch.impl.BytecodePatch
import app.revanced.patches.youtube.misc.forcevp9.annotations.ForceVP9Compatibility
import app.revanced.patches.youtube.misc.forcevp9.fingerprints.ForceVP9CodecFingerprint
import app.revanced.patches.youtube.misc.forcevp9.fingerprints.ForceVP9CodecFingerprintTwo
import app.revanced.patches.youtube.misc.forcevp9.fingerprints.ForceVP9ParentFingerprint

//ToDo: Exclude Patch by default before merging PR
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
        val classDef = ForceVP9ParentFingerprint.result!!.classDef
        ForceVP9CodecFingerprint.resolve(data, classDef)
        ForceVP9CodecFingerprintTwo.resolve(data, classDef)

        replaceInstructions(ForceVP9CodecFingerprint.result!!)
        replaceInstructions(ForceVP9CodecFingerprintTwo.result!!)

        return PatchResultSuccess()
    }

    private fun replaceInstructions(result: MethodFingerprintResult) {
        val method = result.mutableMethod
        method.removeInstructions(0, method.implementation!!.instructions.size - 1)
        method.addInstructions(
            0, """
            invoke-static {}, Lapp/revanced/integrations/patches/ForceCodecPatch;->shouldForceVP9()Z
            move-result v0
            return v0
        """
        )
    }
}
