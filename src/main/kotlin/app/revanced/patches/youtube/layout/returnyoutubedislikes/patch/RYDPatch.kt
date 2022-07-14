package app.revanced.patches.youtube.layout.returnyoutubedislikes.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.impl.BytecodeData
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.Dependencies
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.patch.impl.BytecodePatch
import app.revanced.patches.youtube.layout.returnyoutubedislikes.annotations.RYDCompatibility
import app.revanced.patches.youtube.layout.returnyoutubedislikes.fingerprints.ComponentCreateFingerprint
import app.revanced.patches.youtube.layout.returnyoutubedislikes.fingerprints.DislikeFingerprint
import app.revanced.patches.youtube.layout.returnyoutubedislikes.fingerprints.LikeFingerprint
import app.revanced.patches.youtube.layout.returnyoutubedislikes.fingerprints.RemoveLikeFingerprint
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch

@Patch
@Dependencies(dependencies = [IntegrationsPatch::class])
@Name("return-youtube-dislikes")
@Description("Shows the dislike count of videos.")
@RYDCompatibility
@Version("0.0.1")
class RYDPatch : BytecodePatch(
    listOf(
        ComponentCreateFingerprint, LikeFingerprint, DislikeFingerprint, RemoveLikeFingerprint
    )
) {
    override fun execute(data: BytecodeData): PatchResult {
        LikeFingerprint.result!!.mutableMethod.addInstructions(
            0,
            """
            const/4 v0, 1
            invoke-static {v0}, Lapp/revanced/integrations/ryd/ReturnYouTubeDislikes;->sendVote(I)V
            """
        )
        DislikeFingerprint.result!!.mutableMethod.addInstructions(
            0,
            """
            const/4 v0, -1
            invoke-static {v0}, Lapp/revanced/integrations/ryd/ReturnYouTubeDislikes;->sendVote(I)V
            """
        )
        RemoveLikeFingerprint.result!!.mutableMethod.addInstructions(
            0,
            """
            const/4 v0, 0
            invoke-static {v0}, Lapp/revanced/integrations/ryd/ReturnYouTubeDislikes;->sendVote(I)V
            """
        )

        ComponentCreateFingerprint.result!!.mutableMethod.addInstructions(
            0,
            """
            move-object/from16 v0, p5
            move-object/from16 v1, p17
            invoke-static {v0, v1}, Lapp/revanced/integrations/ryd/ReturnYouTubeDislikes;->onComponentCreated(Ljava/lang/Object;Ljava/util/concurrent/atomic/AtomicReference;)V
        """
        )

        return PatchResultSuccess()
    }
}
