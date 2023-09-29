package app.revanced.patches.tumblr.timelinefilter

import app.revanced.extensions.exception
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.extensions.InstructionExtensions.removeInstructions
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patches.tumblr.timelinefilter.fingerprints.PostsResponseConstructorFingerprint
import app.revanced.patches.tumblr.timelinefilter.fingerprints.TimelineConstructorFingerprint
import app.revanced.patches.tumblr.timelinefilter.fingerprints.TimelineFilterIntegrationFingerprint
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction35c

@Patch(description = "Filter timeline objects.", requiresIntegrations = true)
object TimelineFilterPatch : BytecodePatch(
    setOf(TimelineConstructorFingerprint, TimelineFilterIntegrationFingerprint, PostsResponseConstructorFingerprint)
) {
    /**
     * Add a filter to hide the given timeline object type.
     * The list of all Timeline object types is found in the TimelineObjectType class,
     * where they are mapped from their api name (returned by tumblr via the HTTP API) to the enum value name.
     *
     * @param typeName The enum name of the timeline object type to hide.
     */
    @Suppress("KDocUnresolvedReference")
    internal lateinit var addObjectTypeFilter: (typeName: String) -> Unit private set

    override fun execute(context: BytecodeContext) {

        TimelineFilterIntegrationFingerprint.result?.let { integration ->
            val filterInsertIndex = integration.scanResult.patternScanResult!!.startIndex

            integration.mutableMethod.apply {
                // This is the List.add call from the dummy object type filter
                val instr = getInstruction<BuilderInstruction35c>(filterInsertIndex + 1)

                assert(instr.registerCount == 2)

                // From the dummy filter call, we can get the 2 registers we need to add more filters
                val listRegister = instr.registerC
                val stringRegister = instr.registerD

                // Remove "BLOCKED_OBJECT_DUMMY" object type filter
                removeInstructions(filterInsertIndex, 2)

                addObjectTypeFilter = { typeName ->
                    // The java equivalent of this is
                    //   blockedObjectTypes.add({typeName})
                    addInstructionsWithLabels(
                        filterInsertIndex, """
                            const-string v$stringRegister, "$typeName"
                            invoke-interface { v$listRegister, v$stringRegister }, Ljava/util/List;->add(Ljava/lang/Object;)Z
                        """
                    )
                }
            }
        } ?: throw TimelineFilterIntegrationFingerprint.exception

        mapOf(
            TimelineConstructorFingerprint to 1,
            PostsResponseConstructorFingerprint to 2
        ).forEach { (fingerprint, paramRegister) ->
            fingerprint.result?.mutableMethod?.addInstructions(
                0,
                "invoke-static {p$paramRegister}, Lapp/revanced/tumblr/patches/TimelineFilterPatch;->" +
                        "filterTimeline(Ljava/util/List;)V"
            ) ?: throw fingerprint.exception
        }
    }
}
