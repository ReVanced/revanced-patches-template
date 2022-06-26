package app.revanced.patches.youtube.misc.quality.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.impl.BytecodeData
import app.revanced.patcher.extensions.addInstruction
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.patch.annotations.Dependencies
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.patch.impl.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.fingerprint.method.utils.MethodFingerprintUtils.resolve
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
import app.revanced.patches.youtube.misc.quality.annotations.DefaultVideoQualityCompatibility
import app.revanced.patches.youtube.misc.quality.fingerprints.VideoQualitiesFragmentGetterFingerprint
import app.revanced.patches.youtube.misc.quality.fingerprints.VideoQualityFingerprint
import app.revanced.patches.youtube.misc.quality.fingerprints.VideoQualitySetterFingerprint
import org.jf.dexlib2.iface.instruction.ReferenceInstruction
import org.jf.dexlib2.iface.reference.FieldReference

@Patch
@Dependencies(
    dependencies = [IntegrationsPatch::class]
)
@Name("default-video-quality")
@Description("Adds the ability to select preferred video quality.")
@DefaultVideoQualityCompatibility
@Version("0.0.1")
class DefaultVideoQualityPatch : BytecodePatch(
    listOf(
        VideoQualityFingerprint
    )

) {
    override fun execute(data: BytecodeData): PatchResult {
        VideoQualityFingerprint.resolve(data, VideoQualitiesFragmentGetterFingerprint.result!!.classDef)
        VideoQualityFingerprint.resolve(data, VideoQualitySetterFingerprint.result!!.classDef)

        val qualityClass = VideoQualitySetterFingerprint.result!!.classDef
        val qualityFieldReference =
            VideoQualitySetterFingerprint.result!!.mutableMethod.let { method ->
                method.implementation!!.instructions.elementAt(0) as ReferenceInstruction
            }.let { reference ->
                reference as FieldReference
            }

        VideoQualitySetterFingerprint.result!!.mutableMethod.addInstructions(
            0, """
                iget-object v0, p0, ${qualityClass.type}->${qualityFieldReference.name}:${qualityFieldReference.type}
		        invoke-static {p1, p2, v0}, Lapp/revanced/integrations/videoplayer/videosettings/VideoQuality;->setVideoQuality([Ljava/lang/Object;ILjava/lang/Object;)I
   		        move-result p2
            """
        )

        // user selected new video quality
        VideoQualitiesFragmentGetterFingerprint.result!!.mutableMethod.addInstruction(
            0,
            " invoke-static {}, Lapp/revanced/integrations/videoplayer/videosettings/VideoQuality;->userChangedQuality()V"
        )

        return PatchResultSuccess()
    }
}
