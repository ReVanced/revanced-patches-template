package app.revanced.patches.youtube.layout.returnyoutubedislike.patch

import app.revanced.extensions.toErrorResult
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.data.toMethodWalker
import app.revanced.patcher.extensions.MethodFingerprintExtensions.name
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint.Companion.resolve
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultError
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import app.revanced.patches.youtube.layout.returnyoutubedislike.annotations.ReturnYouTubeDislikeCompatibility
import app.revanced.patches.youtube.layout.returnyoutubedislike.fingerprints.*
import app.revanced.patches.youtube.layout.returnyoutubedislike.resource.patch.ReturnYouTubeDislikeResourcePatch
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
import app.revanced.patches.youtube.misc.playertype.patch.PlayerTypeHookPatch
import app.revanced.patches.youtube.misc.video.videoid.patch.VideoIdPatch
import org.jf.dexlib2.builder.instruction.BuilderInstruction35c
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction

@Patch
@DependsOn(
    [
        IntegrationsPatch::class,
        VideoIdPatch::class,
        ReturnYouTubeDislikeResourcePatch::class,
        PlayerTypeHookPatch::class,
    ]
)
@Name("return-youtube-dislike")
@Description("Shows the dislike count of videos using the Return YouTube Dislike API.")
@ReturnYouTubeDislikeCompatibility
@Version("0.0.1")
class ReturnYouTubeDislikePatch : BytecodePatch(
    listOf(
        TextComponentSpecParentFingerprint,
        ShortsTextComponentParentFingerprint,
        LikeFingerprint,
        DislikeFingerprint,
        RemoveLikeFingerprint,
    )
) {

    private companion object {
        const val INTEGRATIONS_PATCH_CLASS_DESCRIPTOR =
            "Lapp/revanced/integrations/patches/ReturnYouTubeDislikePatch;"
    }

    override fun execute(context: BytecodeContext): PatchResult {
        listOf(
            LikeFingerprint.toPatch(Vote.LIKE),
            DislikeFingerprint.toPatch(Vote.DISLIKE),
            RemoveLikeFingerprint.toPatch(Vote.REMOVE_LIKE)
        ).forEach { (fingerprint, vote) ->
            with(fingerprint.result ?: return PatchResultError("Failed to find ${fingerprint.name} method.")) {
                mutableMethod.addInstructions(
                    0,
                    """
                    const/4 v0, ${vote.value}
                    invoke-static {v0}, $INTEGRATIONS_PATCH_CLASS_DESCRIPTOR->sendVote(I)V
                    """
                )
            }
        }

        ShortsTextComponentParentFingerprint.result?.let {
            with(context
                .toMethodWalker(it.method)
                .nextMethod(it.scanResult.patternScanResult!!.endIndex, true)
                .getMethod() as MutableMethod
            ) {
                // After walking, verify the found method is what's expected.
                if (returnType != ("Ljava/lang/CharSequence;") || parameterTypes.size != 1) {
                    return PatchResultError(
                        "Method signature did not match: $this $parameterTypes"
                    )
                }

                val insertInstructions = implementation!!.instructions
                val insertIndex = insertInstructions.size - 1
                val spannedParameterRegister = (insertInstructions.elementAt(insertIndex) as OneRegisterInstruction).registerA
                val existingInstructionReference = (insertInstructions.elementAt(insertIndex - 2) as BuilderInstruction35c).reference
                if (!existingInstructionReference.toString().endsWith("Landroid/text/Spanned;")) {
                    return PatchResultError(
                        "Method signature parameter did not match: $existingInstructionReference"
                    )
                }

                addInstructions(
                    insertIndex, """
                        invoke-static {v$spannedParameterRegister}, $INTEGRATIONS_PATCH_CLASS_DESCRIPTOR->onShortsComponentCreated(Landroid/text/Spanned;)Landroid/text/Spanned;
                        move-result-object v$spannedParameterRegister
                    """
                )
            }

        } ?: return ShortsTextComponentParentFingerprint.toErrorResult()

        VideoIdPatch.injectCall("$INTEGRATIONS_PATCH_CLASS_DESCRIPTOR->newVideoLoaded(Ljava/lang/String;)V")

        with(TextComponentFingerprint
            .apply { resolve(context, TextComponentSpecParentFingerprint.result!!.classDef) }
            .result ?: return TextComponentFingerprint.toErrorResult()
        ) {
            val createComponentMethod = mutableMethod

            val conversionContextParam = 5
            val textRefParam = createComponentMethod.parameters.size - 2
            // Insert index must be 0, otherwise UI does not updated correctly in some situations
            // such as switching from full screen or when using previous/next overlay buttons.
            val insertIndex = 0

            createComponentMethod.addInstructions(
                insertIndex,
                """
                    move-object/from16 v7, p$conversionContextParam
                    move-object/from16 v8, p$textRefParam
                    invoke-static {v7, v8}, $INTEGRATIONS_PATCH_CLASS_DESCRIPTOR->onComponentCreated(Ljava/lang/Object;Ljava/util/concurrent/atomic/AtomicReference;)V
                """
            )
        }
        return PatchResultSuccess()
    }

    private fun MethodFingerprint.toPatch(voteKind: Vote) = VotePatch(this, voteKind)

    private data class VotePatch(val fingerprint: MethodFingerprint, val voteKind: Vote)

    private enum class Vote(val value: Int) {
        LIKE(1),
        DISLIKE(-1),
        REMOVE_LIKE(0)
    }
}
