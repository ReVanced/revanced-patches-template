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
import app.revanced.patches.youtube.misc.quality.fingerprints.VideoQualityReferenceFingerprint
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
        VideoQualitySetterFingerprint,
    )

) {
    override fun execute(data: BytecodeData): PatchResult {

        val qualityClass = VideoQualitySetterFingerprint.result!!.classDef
        val setterMethod = VideoQualitySetterFingerprint.result!!

        VideoQualitiesFragmentGetterFingerprint.resolve(data, VideoQualitySetterFingerprint.result!!.classDef)
        val fragmentMethod = VideoQualitiesFragmentGetterFingerprint.result!!

        VideoQualityReferenceFingerprint.resolve(data, VideoQualitySetterFingerprint.result!!.classDef)
        val qualityFieldReference =
            VideoQualityReferenceFingerprint.result!!.mutableMethod.let { method ->
                method.implementation!!.instructions.elementAt(0) as ReferenceInstruction // TODO:fix this
            }.let { reference ->
                reference as FieldReference
            }


        setterMethod.mutableMethod.addInstructions(
            0, """
                iget-object v0, p0, ${qualityClass.type}->${qualityFieldReference.name}:${qualityFieldReference.type}
		        invoke-static {p1, p2, v0}, Lapp/revanced/integrations/videoplayer/videosettings/VideoQuality;->setVideoQuality([Ljava/lang/Object;ILjava/lang/Object;)I
   		        move-result p2
            """,
        )

        fragmentMethod.mutableMethod.addInstruction(
            0,
            "invoke-static {}, Lapp/revanced/integrations/videoplayer/videosettings/VideoQuality;->userChangedQuality()V"
        )
        
        return PatchResultSuccess()
    }
}
