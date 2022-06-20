package app.revanced.patches.youtube.misc.quality.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.implementation.BytecodeData
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.patch.annotations.Dependencies
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.patch.implementation.BytecodePatch
import app.revanced.patcher.patch.implementation.misc.PatchResult
import app.revanced.patcher.patch.implementation.misc.PatchResultSuccess
import app.revanced.patcher.util.smali.toInstruction
import app.revanced.patcher.util.smali.toInstructions
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
import app.revanced.patches.youtube.misc.quality.annotations.DefaultVideoQualityCompatibility
import app.revanced.patches.youtube.misc.quality.signatures.VideoQualitiesFragmentGetterSignature
import app.revanced.patches.youtube.misc.quality.signatures.VideoQualitySetterSignature
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
    listOf(VideoQualitiesFragmentGetterSignature)
) {
    override fun execute(data: BytecodeData): PatchResult {
        val signatureResolverResult = VideoQualitiesFragmentGetterSignature.result!!
        val qualityClass = signatureResolverResult.definingClassProxy.resolve()

        // get the field where the current video quality is stored
        val qualityFieldReference =
            signatureResolverResult.findParentMethod(VideoQualitySetterSignature)!!.immutableMethod.let { method ->
                method.implementation!!.instructions.elementAt(0) as ReferenceInstruction
            }.let { reference ->
                reference as FieldReference
            }

        // override the quality in this method
        val videoQualitySelectorMethod = qualityClass.methods.find {
            it.parameterTypes.any { parameter -> parameter.startsWith("[L") }
        }!!
        videoQualitySelectorMethod.implementation!!.addInstructions(
            0, """
                iget-object v0, p0, ${qualityClass.type}->${qualityFieldReference.name}:${qualityFieldReference.type}
		        invoke-static {p1, p2, v0}, Lfi/razerman/youtube/videosettings/VideoQuality;->setVideoQuality([Ljava/lang/Object;ILjava/lang/Object;)I
   		        move-result p2
            """.trimIndent().toInstructions("IIZI", 7, false)
        )

        // user selected new video quality
        qualityClass.methods.first { it.name == "onItemClick" }.implementation!!.addInstruction(
            0, """
                invoke-static {}, Lfi/razerman/youtube/videosettings/VideoQuality;->userChangedQuality()V
	        """.trimIndent().toInstruction()
        )

        return PatchResultSuccess()
    }
}
