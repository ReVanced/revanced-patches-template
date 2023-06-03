package app.revanced.patches.reddit.testing.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.extensions.or
import app.revanced.patcher.extensions.removeInstruction
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprintResult
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod.Companion.toMutable
import app.revanced.patches.reddit.testing.annotations.AdPostCompatibility
import app.revanced.patches.reddit.testing.fingerprints.AdPostFingerprint
import app.revanced.patches.reddit.testing.fingerprints.NewAdPostFingerprint
import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.Opcode
import org.jf.dexlib2.builder.MutableMethodImplementation
import org.jf.dexlib2.dexbacked.reference.DexBackedFieldReference
import org.jf.dexlib2.iface.instruction.formats.Instruction22c
import org.jf.dexlib2.iface.instruction.formats.Instruction35c
import org.jf.dexlib2.immutable.ImmutableMethod
import org.jf.dexlib2.immutable.ImmutableMethodParameter

@Patch
@Name("hide-promoted")
@Description("Removes promoted posts in all feeds.")
@AdPostCompatibility
@Version("0.0.1")
class TotalAdPostPatch : BytecodePatch(
    listOf(AdPostFingerprint, NewAdPostFingerprint)
) {
    /**
     * Adds the method filterChildren(List<Any>): List<Any>
     * to filter away all ILinks which are labeled as promoted.
     **/
    private fun addFilterChildrenMethod(result: MethodFingerprintResult) {
        val filteringMethod = ImmutableMethod(
            result.method.definingClass,
            "filterChildren",
            listOf(ImmutableMethodParameter("Ljava/util/List;", null, "children")),
            "Ljava/util/List;",
            AccessFlags.PRIVATE or AccessFlags.FINAL,
            null,
            null,
            MutableMethodImplementation(4)
        ).toMutable().apply {
            /*
            // Can be summarized as the following
            fun filterChildren(p1: Iterable<Any>): List<Any> {
                val filteredList = mutableListOf<Any>()
                val iterator = p1.iterator()
                while (iterator.hasNext()) {
                    val item = iterator.next()

                    if (item is ILink) {
                        if (!item.getPromoted()) {
                            filteredList.add(item)
                        }
                    }
                }
                return filteredList
            }
            */
            addInstructions(
                0,
                """
                # p1 is the original list
                check-cast p1, Ljava/lang/Iterable;
            
                # Create a new list to store filtered children
                new-instance v1, Ljava/util/ArrayList;
                invoke-direct {v1}, Ljava/util/ArrayList;-><init>()V
                check-cast v1, Ljava/util/Collection;
                
                # v3 = p1.iterator()
                invoke-interface {p1}, Ljava/lang/Iterable;->iterator()Ljava/util/Iterator;
                move-result-object v3
                :continue
            
                # Check if the iterator has next
                invoke-interface {v3}, Ljava/util/Iterator;->hasNext()Z
                move-result v2
            
                # Exit loop if no next
                if-eqz v2, :exit_loop
                invoke-interface {v3}, Ljava/util/Iterator;->next()Ljava/lang/Object;

                # Check if the object is an instance of ILink
                move-result-object v2
                instance-of v0, v2, Lcom/reddit/domain/model/ILink;
                if-eqz v0, :afterLink

                # ---------------------------------------
                # If it's an ILink object, check if it's promoted
                check-cast v2, Lcom/reddit/domain/model/ILink;
                invoke-virtual {v2}, Lcom/reddit/domain/model/ILink;->getPromoted()Z
                move-result v0
            
                # Continue loop if it's promoted
                if-nez v0, :continue
                # ---------------------------------------
                :afterLink
                
                # Add the object to the filtered list
                invoke-interface {v1, v2}, Ljava/util/Collection;->add(Ljava/lang/Object;)Z
                goto :continue
            
                :exit_loop
                # Return the filtered list
                return-object v1
                """
            )
        }
        result.mutableClass.methods.add(filteringMethod)
    }

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
        addFilterChildrenMethod(result)

        // Looks for an iput-object with the variable name children:
        // The children attribute is a list of posts, which need filtering.
        // iput-object p1, p0, Lcom/reddit/domain/model/listing/Listing;->children:Ljava/util/List;
        val instruction = method.implementation!!.instructions.withIndex().first {
            it.value.opcode == Opcode.IPUT_OBJECT
                    && (((it.value as? Instruction22c)?.reference as? DexBackedFieldReference)?.name == "children")
        }
        val castedInstruction = instruction.value as Instruction22c

        // Before:
        // this.children = p1
        // After:
        // this.children = filterChildren(p1)
        method.removeInstruction(instruction.index)
        val filterCall =
            """
            invoke-direct {p0, v${castedInstruction.registerA}}, ${result.mutableMethod.definingClass}->filterChildren(Ljava/util/List;)Ljava/util/List;
            move-result-object v0
            iput-object v0, v${castedInstruction.registerB}, ${castedInstruction.reference}
            """
        method.addInstructions(instruction.index, filterCall)
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

    override fun execute(context: BytecodeContext): PatchResult {
        applyOldPatch()
        applyNewPatch()
        return PatchResultSuccess()
    }
}
