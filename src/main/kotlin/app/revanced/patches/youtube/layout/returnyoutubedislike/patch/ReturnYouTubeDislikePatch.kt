package app.revanced.patches.youtube.layout.returnyoutubedislike.patch

import app.revanced.extensions.toErrorResult
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.data.toMethodWalker
import app.revanced.patcher.extensions.*
import app.revanced.patcher.extensions.MethodFingerprintExtensions.name
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
import org.jf.dexlib2.iface.instruction.TwoRegisterInstruction

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
        TextComponentConstructorFingerprint,
        TextComponentSpecFingerprint,
        ShortsTextComponentParentFingerprint,
        LikeFingerprint,
        DislikeFingerprint,
        RemoveLikeFingerprint,
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {
        // region Inject newVideoLoaded event handler

        VideoIdPatch.injectCall("$INTEGRATIONS_PATCH_CLASS_DESCRIPTOR->newVideoLoaded(Ljava/lang/String;)V")


        listOf(
            LikeFingerprint.toPatch(Vote.LIKE),
            DislikeFingerprint.toPatch(Vote.DISLIKE),
            RemoveLikeFingerprint.toPatch(Vote.REMOVE_LIKE)
        ).forEach { (fingerprint, vote) ->
            fingerprint.result?.mutableMethod?.apply {
                addInstructions(
                    0,
                    """
                    const/4 v0, ${vote.value}
                    invoke-static {v0}, $INTEGRATIONS_PATCH_CLASS_DESCRIPTOR->sendVote(I)V
                    """
                )
            } ?: return PatchResultError("Failed to find ${fingerprint.name} method.")
        }


        // This hook does not correctly handle scrolling off/on screen,
        // but it does correctly handle when the user dislikes a video.
        TextComponentContextFingerprint.also {
            it.resolve(
                context,
                TextComponentConstructorFingerprint.result!!.classDef
            )
        }.result?.let { result ->
            // match two locations in the same method
            val conversionContextIndex = result.scanResult.patternScanResult!!.startIndex

            val spanReferenceEndIndex = TextComponentAtomicReferenceFingerprint.also {
                if (!TextComponentAtomicReferenceFingerprint.resolve(context, result.method, result.classDef))
                    return TextComponentAtomicReferenceFingerprint.toErrorResult()
            }.result!!.scanResult.patternScanResult!!.endIndex

            result.mutableMethod.apply {
                val conversionContextRegister =
                    (instruction(conversionContextIndex) as TwoRegisterInstruction).registerA
                val atomicReferenceRegister =
                    (instruction(spanReferenceEndIndex) as TwoRegisterInstruction).registerB
                addInstruction(
                    spanReferenceEndIndex + 1,
                    "invoke-static {v$conversionContextRegister, v$atomicReferenceRegister}, $INTEGRATIONS_PATCH_CLASS_DESCRIPTOR->onComponentCreated(Ljava/lang/Object;Ljava/util/concurrent/atomic/AtomicReference;)V"
                )
            }
        } ?: return TextComponentContextFingerprint.toErrorResult()


        // Handles all cases, except when a user dislikes a video
        TextComponentSpecFingerprint.result?.let {
            with(it.mutableMethod) {
                val endIndex = it.scanResult.patternScanResult!!.endIndex
                val returnInstruction = (instruction(endIndex) as OneRegisterInstruction)
                val spannedStringRegister = returnInstruction.registerA
                val contextTempRegister = if (spannedStringRegister == 0) 1 else 0

                // Must replace the return instruction (and not insert before), as it has a label attached to it.
                // Replacing the last instruction with multiple instructions throws an array index out of bounds,
                // So replace one line and insert the remaining instructions.
                replaceInstructions(endIndex, "move-object/from16 v$contextTempRegister, p0")
                addInstructions(endIndex + 1, """
                        invoke-static {v$contextTempRegister, v$spannedStringRegister}, $INTEGRATIONS_PATCH_CLASS_DESCRIPTOR->onComponentCreated(Ljava/lang/Object;Landroid/text/SpannableString;)Landroid/text/SpannableString;
                        move-result-object v$spannedStringRegister
                        return-object v$spannedStringRegister
                    """
                )
            }
        } ?: return TextComponentSpecFingerprint.toErrorResult()


        ShortsTextComponentParentFingerprint.result?.let {
            context
                .toMethodWalker(it.method)
                .nextMethod(it.scanResult.patternScanResult!!.endIndex, true)
                .getMethod().let { method ->
                    with(method as MutableMethod) {
                        // After walking, verify the found method is what's expected.
                        if (returnType != ("Ljava/lang/CharSequence;") || parameterTypes.size != 1)
                            return PatchResultError("Method signature did not match: $this $parameterTypes")

                        val insertIndex = implementation!!.instructions.size - 1
                        val spannedParameterRegister = (instruction(insertIndex) as OneRegisterInstruction).registerA
                        val parameter = (instruction(insertIndex - 2) as BuilderInstruction35c).reference

                        if (!parameter.toString().endsWith("Landroid/text/Spanned;"))
                            return PatchResultError("Method signature parameter did not match: $parameter")

                        insertShorts(insertIndex, spannedParameterRegister)
                    }
                }

            // Additional hook, called after user dislikes.
            with (it.mutableMethod) {
                val insertIndex = it.scanResult.patternScanResult!!.startIndex + 2
                val insertRegister = (implementation!!.instructions.elementAt(insertIndex - 1)
                        as OneRegisterInstruction).registerA
                insertShorts(insertIndex, insertRegister)
            }
        } ?: return ShortsTextComponentParentFingerprint.toErrorResult()


        return PatchResultSuccess()
    }

    private companion object {
        const val INTEGRATIONS_PATCH_CLASS_DESCRIPTOR =
            "Lapp/revanced/integrations/returnyoutubedislike/ReturnYouTubeDislike;"

        private fun MethodFingerprint.toPatch(voteKind: Vote) = VotePatch(this, voteKind)
    }

    private fun MutableMethod.insertShorts(index: Int, register: Int) {
        addInstructions(index, """
                invoke-static {v$register}, $INTEGRATIONS_PATCH_CLASS_DESCRIPTOR->onShortsComponentCreated(Landroid/text/Spanned;)Landroid/text/Spanned;
                move-result-object v$register
            """
        )
    }

    private data class VotePatch(val fingerprint: MethodFingerprint, val voteKind: Vote)

    private enum class Vote(val value: Int) {
        LIKE(1),
        DISLIKE(-1),
        REMOVE_LIKE(0)
    }
}
