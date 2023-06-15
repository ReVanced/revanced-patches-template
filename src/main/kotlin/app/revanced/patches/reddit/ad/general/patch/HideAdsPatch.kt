package app.revanced.patches.reddit.ad.general.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.extensions.InstructionExtensions.removeInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.patch.annotations.RequiresIntegrations
import app.revanced.patches.reddit.ad.banner.patch.HideBannerPatch
import app.revanced.patches.reddit.ad.comments.patch.HideCommentAdsPatch
import app.revanced.patches.reddit.ad.general.annotations.HideAdsCompatibility
import app.revanced.patches.reddit.ad.general.fingerprints.AdPostFingerprint
import app.revanced.patches.reddit.ad.general.fingerprints.NewAdPostFingerprint
import org.jf.dexlib2.Opcode
import org.jf.dexlib2.iface.instruction.ReferenceInstruction
import org.jf.dexlib2.iface.instruction.formats.Instruction22c
import org.jf.dexlib2.iface.reference.FieldReference
import org.jf.dexlib2.iface.reference.MethodReference

@Patch
@Name("hide-ads")
@Description("Removes ads from the Reddit.")
@DependsOn([HideBannerPatch::class, HideCommentAdsPatch::class])
@HideAdsCompatibility
@RequiresIntegrations
@Version("0.0.2")
class HideAdsPatch : BytecodePatch(
    listOf(AdPostFingerprint, NewAdPostFingerprint)
) {
    override fun execute(context: BytecodeContext): PatchResult {
        // region Filter promoted ads (does not work in popular or latest feed)

        AdPostFingerprint.result?.mutableMethod?.apply {
            val setPostsListChildren = implementation!!.instructions.first { instruction ->
                if (instruction.opcode != Opcode.IPUT_OBJECT) return@first false

                val reference = (instruction as ReferenceInstruction).reference as FieldReference
                reference.name == "children"
            }

            val castedInstruction = setPostsListChildren as Instruction22c
            val itemsRegister = castedInstruction.registerA
            val listInstanceRegister = castedInstruction.registerB

            // postsList.children = filterChildren(postListItems)
            removeInstruction(setPostsListChildren.location.index)
            addInstructions(
                setPostsListChildren.location.index,
                """
                    invoke-static {v$itemsRegister}, $FILTER_METHOD_DESCRIPTOR
                    move-result-object v0
                    iput-object v0, v$listInstanceRegister, ${castedInstruction.reference}
                """
            )
        }

        // endregion

        // region Remove ads from popular and latest feed

        NewAdPostFingerprint.result?.let { result ->
            // The new feeds work by inserting posts into lists.
            // AdElementConverter is conveniently responsible for inserting all feed ads.
            // By removing the appending instruction no ad posts gets appended to the feed.
            val index = result.method.implementation!!.instructions.indexOfFirst {
                if (it.opcode != Opcode.INVOKE_VIRTUAL) return@indexOfFirst false

                val reference = (it as ReferenceInstruction).reference as MethodReference

                reference.name == "add" && reference.definingClass == "Ljava/util/ArrayList;"
            }

            result.mutableMethod.removeInstruction(index)
        }

        // endregion

        return PatchResultSuccess()
    }

    private companion object {
        private const val FILTER_METHOD_DESCRIPTOR =
            "Lapp/revanced/reddit/patches/FilterPromotedLinksPatch;" +
                    "->filterChildren(Ljava/lang/Iterable;)Ljava/util/List;"
    }
}
