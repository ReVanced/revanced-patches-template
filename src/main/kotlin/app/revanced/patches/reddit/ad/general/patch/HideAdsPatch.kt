package app.revanced.patches.reddit.ad.general.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.extensions.removeInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.patch.annotations.RequiresIntegrations
import app.revanced.patches.reddit.ad.general.fingerprints.AdPostFingerprint
import app.revanced.patches.reddit.ad.general.annotations.HideAdsCompatibility
import app.revanced.patches.reddit.ad.general.fingerprints.NewAdPostFingerprint
import org.jf.dexlib2.Opcode
import org.jf.dexlib2.builder.instruction.BuilderInstruction21c
import org.jf.dexlib2.iface.instruction.ReferenceInstruction
import org.jf.dexlib2.iface.instruction.formats.Instruction22c
import org.jf.dexlib2.iface.instruction.formats.Instruction35c
import org.jf.dexlib2.iface.reference.FieldReference
import org.jf.dexlib2.iface.reference.StringReference
import org.jf.dexlib2.immutable.reference.ImmutableStringReference

@Patch
@Name("hide-ads")
@Description("Removes general ads from the Reddit frontpage and subreddits.")
@HideAdsCompatibility
@RequiresIntegrations
@Version("0.0.2")
class HideAdsPatch : BytecodePatch(
    listOf(AdPostFingerprint, NewAdPostFingerprint)
) {
    /**
     * Applies an older patch which removes almost all promoted posts.
     * Does not work on Popular and Latest.
     *
     * Almost all posts are represented in the reddit code with the Link class.
     * The Link class is wrapped in a Listing, which contains a list of posts.
     * By filtering the listing it becomes possible to remove any promoted posts.
     **/
    private fun applyOldPatch() {
        val result = AdPostFingerprint.result!!
        val method = result.mutableMethod

        // Looks for an iput-object with the variable name children:
        // The children attribute is a list of posts, which need filtering.
        // iput-object p1, p0, Lcom/reddit/domain/model/listing/Listing;->children:Ljava/util/List;
        val instruction = method.implementation!!.instructions.first { instruction ->
            instruction.opcode == Opcode.IPUT_OBJECT
                    && ((instruction as? ReferenceInstruction)?.reference as? FieldReference)?.name == "children"
        }
        val castedInstruction = instruction as Instruction22c

        // Before:
        // this.children = p1
        // After:
        // this.children = filterChildren(p1)
        method.removeInstruction(instruction.location.index)
        val filterCall =
            """
            invoke-static {v${castedInstruction.registerA}}, $FILTER_METHOD_DESCRIPTOR
            move-result-object v0
            iput-object v0, v${castedInstruction.registerB}, ${castedInstruction.reference}
            """
        method.addInstructions(instruction.location.index, filterCall)
    }

    /**
     * Removes all post ads in the reworked feeds, e.g Popular and Latest.
     * They function differently internally which requires a separate patch.
     **/
    private fun applyNewPatch() {
        val result = NewAdPostFingerprint.result!!
        // The new feeds work by inserting posts into lists.
        // AdElementConverter is conveniently responsible for inserting all feed ads.
        // By removing the appending instruction no ad posts gets appended to the feed.
        val index = result.method.implementation!!.instructions.indexOfFirst {
            it.opcode == Opcode.INVOKE_VIRTUAL &&
                    (it as? Instruction35c)?.reference?.toString()
                        ?.startsWith("Ljava/util/ArrayList;->add") == true
        }
        result.mutableMethod.removeInstruction(index)
    }

    private fun applyStringPatch(context: BytecodeContext) {
        context.classes.forEach { classDef ->
            classDef.methods.forEach methodLoop@{ method ->
                val implementation = method.implementation ?: return@methodLoop

                implementation.instructions.forEachIndexed { i, instruction ->
                    if (instruction.opcode != Opcode.CONST_STRING) return@forEachIndexed
                    if (((instruction as ReferenceInstruction).reference as StringReference).string != "AdPost") return@forEachIndexed

                    val proxiedClass = context.proxy(classDef).mutableClass

                    val proxiedImplementation = proxiedClass.methods.first {
                        it.name == method.name && it.parameterTypes.containsAll(method.parameterTypes)
                    }.implementation!!

                    var newString = "AdPost1"
                    if (proxiedImplementation.instructions[i - 1].opcode == Opcode.CONST_STRING) {
                        newString = "SubredditPost"
                    }
                    proxiedImplementation.replaceInstruction(
                        i, BuilderInstruction21c(
                            Opcode.CONST_STRING,
                            (proxiedImplementation.instructions[i] as BuilderInstruction21c).registerA,
                            ImmutableStringReference(newString)
                        )
                    )
                }
            }
        }
    }

    override fun execute(context: BytecodeContext): PatchResult {
        applyStringPatch(context)
        applyOldPatch()
        applyNewPatch()
        return PatchResultSuccess()
    }

    private companion object {
        private const val FILTER_METHOD_DESCRIPTOR =
            "Lapp/revanced/reddit/patches/FilterPromotedLinksPatch;" +
                    "->filterChildren(Ljava/lang/Iterable;)Ljava/util/List;"
    }
}
