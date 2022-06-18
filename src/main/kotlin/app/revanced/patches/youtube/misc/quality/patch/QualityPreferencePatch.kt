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
import app.revanced.patches.youtube.misc.quality.annotations.QualityPreferenceCompatibility
import app.revanced.patches.youtube.misc.quality.signatures.QualityPreferenceSetSignature
import app.revanced.patches.youtube.misc.quality.signatures.QualityPreferenceSignature
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch

@Patch
@Dependencies(
    dependencies = [IntegrationsPatch::class, SettingsResourcePatch::class]
)
@Name("quality-preference")
@Description("Adds the ability to select preferred video quality.")
@QualityPreferenceCompatibility
@Version("0.0.1")
class QualityPreferencePatch : BytecodePatch(
    listOf(
        QualityPreferenceSetSignature, QualityPreferenceSignature
    )
) {
    override fun execute(data: BytecodeData): PatchResult {
        val result1 = signatures.first().result!!
        val implementation1 = result1.method.implementation!!

        val result2 = signatures.last().result!!
        val implementation2 = result2.method.implementation!!

        implementation1.addInstruction(
            0, """
            invoke-static {}, Lfi/razerman/youtube/videosettings/VideoQuality;->userChangedQuality()V
	      """.trimIndent().toInstruction()
        )

        implementation2.addInstructions(
            0, """
		   iget-object v0, p0, Lkdy;->an:Lzsk;
		   invoke-static {p1, p2, v0}, Lfi/razerman/youtube/videosettings/VideoQuality;->setVideoQuality([Ljava/lang/Object;ILjava/lang/Object;)I
   		   move-result p2
	         """.trimIndent().toInstructions()
        )

        return PatchResultSuccess()
    }
}
