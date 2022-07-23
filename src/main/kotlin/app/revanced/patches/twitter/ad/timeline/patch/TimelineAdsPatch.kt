package app.revanced.patches.twitter.ad.timeline.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.impl.BytecodeData
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.extensions.instruction
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprintResult
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultError
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.patch.impl.BytecodePatch
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import app.revanced.patches.twitter.ad.timeline.annotations.TimelineAdsCompatibility
import app.revanced.patches.twitter.ad.timeline.fingerprints.TimelineRtbImageAdParserFingerprint
import app.revanced.patches.twitter.ad.timeline.fingerprints.TimelineTweetJsonParserFingerprint
import org.jf.dexlib2.Opcode
import org.jf.dexlib2.builder.BuilderInstruction
import org.jf.dexlib2.builder.instruction.BuilderInstruction22c
import org.jf.dexlib2.iface.instruction.ReferenceInstruction
import org.jf.dexlib2.iface.reference.FieldReference
import org.jf.dexlib2.iface.reference.StringReference

@Patch
@Name("timeline-ads")
@Description("Removes ads from the Twitter timeline.")
@TimelineAdsCompatibility
@Version("0.0.1")
class TimelineAdsPatch : BytecodePatch(
    listOf(TimelineTweetJsonParserFingerprint/*, TimelineRtbImageAdParserFingerprint*/)
) {
    override fun execute(data: BytecodeData): PatchResult {
        if (removePromotedAds())
            return PatchResultError("The instruction for the tweet id field could not be found")

        /*removeRtbImageAds()*/

        return PatchResultSuccess()
    }

    private fun removeRtbImageAds(): Boolean {
        val (parserFingerprintResult, parserMethod, _) = TimelineRtbImageAdParserFingerprint.unwrap()

        val nullRegister = 1 // This register stores the null value
        val creativeIdSetterIndex =
            parserFingerprintResult.patternScanResult!!.startIndex // At this index the createId field is set

        // Create the modified instruction
        val originalInstruction = parserMethod.instruction(creativeIdSetterIndex) as BuilderInstruction22c
        val modifiedInstruction = BuilderInstruction22c(
            Opcode.IPUT_OBJECT,
            nullRegister,
            originalInstruction.registerB,
            originalInstruction.reference
        )

        // Replace the original instruction with the modified one
        // This will null the creativeId field
        parserMethod.implementation!!.replaceInstruction(
            creativeIdSetterIndex,
            modifiedInstruction
        )

        return true
    }

    private fun removePromotedAds(): Boolean {
        val (parserFingerprintResult, parserMethod, instructions) = TimelineTweetJsonParserFingerprint.unwrap()

        // Anchor index
        val tweetIdFieldInstructionIndex = instructions.indexOfFirst { instruction ->
            if (instruction.opcode.ordinal != Opcode.CONST_STRING.ordinal) return@indexOfFirst false
            if (((instruction as? ReferenceInstruction)?.reference as StringReference).string != "tweetSocialProof") return@indexOfFirst false

            // Use the above conditions as an anchor to find the index for the instruction with the field we need
            return@indexOfFirst true
        } - 2 // This is where the instruction with the field is located

        // Reference to the tweetId field for of the timeline tweet
        val tweetIdFieldReference =
            (parserMethod.instruction(tweetIdFieldInstructionIndex) as? BuilderInstruction22c)?.reference as? FieldReference
                ?: return true

        // Set the tweetId field to null
        // This will cause twitter to not show the promoted ads, because we set it to null, when the tweet is promoted
        parserFingerprintResult.mutableMethod.addInstructions(
            parserFingerprintResult.patternScanResult!!.startIndex + 1,
            """
                    const/4 v2, 0x0
                    iput-object v2, p0, Lcom/twitter/model/json/timeline/urt/JsonTimelineTweet;->${tweetIdFieldReference.name}:Ljava/lang/String;
                """
        )
        return false
    }

    private fun MethodFingerprint.unwrap(): Triple<MethodFingerprintResult, MutableMethod, MutableList<BuilderInstruction>> {
        val parserFingerprintResult = this.result!!
        val parserMethod = parserFingerprintResult.mutableMethod
        val instructions = parserMethod.implementation!!.instructions

        return Triple(parserFingerprintResult, parserMethod, instructions)
    }
}
