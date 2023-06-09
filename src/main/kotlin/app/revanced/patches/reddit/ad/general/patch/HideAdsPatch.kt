package app.revanced.patches.reddit.ad.general.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.extensions.removeInstruction
import app.revanced.patcher.extensions.replaceInstructions
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
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction
import org.jf.dexlib2.iface.instruction.ReferenceInstruction
import org.jf.dexlib2.iface.instruction.formats.Instruction22c
import org.jf.dexlib2.iface.reference.FieldReference
import org.jf.dexlib2.iface.reference.MethodReference
import org.jf.dexlib2.iface.reference.StringReference
import org.jf.dexlib2.util.MethodUtil

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
        // region Remove general ads from Frontpage and Subreddits.

        context.classes.forEach { classDef ->
            classDef.methods.forEach methodLoop@{ method ->
                method.implementation?.instructions?.forEachIndexed { i, instruction ->
                    // Find instructions that need to be replaced.
                    if (instruction.opcode != Opcode.CONST_STRING)
                        return@forEachIndexed
                    if (((instruction as ReferenceInstruction).reference as StringReference).string != "AdPost")
                        return@forEachIndexed

                    context.proxy(classDef).mutableClass.methods.first {
                        MethodUtil.methodSignaturesMatch(it, method)
                    }.apply {
                        // TODO: Figure out the trick behind this.
                        // Probably in some cases AdPost is accompanied by another string right before it.
                        // In this case we replace the string with SubredditPost.
                        // The only cases where this was the case, was when the previous string was "SubredditPost",
                        // so the string and the previous string are the same,
                        // or else the string is modified to be "AdPost1", which is unclear what it is.
                        val newString = if (implementation!!.instructions[i - 1].opcode == Opcode.CONST_STRING) {
                            "SubredditPost"
                        } else {
                            "AdPost1"
                        }

                        val originalRegister =
                            (implementation!!.instructions[i] as OneRegisterInstruction).registerA

                        replaceInstructions(i, "const-string v$originalRegister, \"$newString\"")
                    }
                }
            }
        }

        // endregion

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

                reference.name == "add" && reference.definingClass == "Lava/util/ArrayList;"
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
