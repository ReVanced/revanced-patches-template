package app.revanced.patches.tumblr.timelinefilter

import app.revanced.extensions.exception
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.revanced.patcher.extensions.InstructionExtensions.removeInstructions
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patches.tumblr.timelinefilter.fingerprints.PostsResponseConstructorFingerprint
import app.revanced.patches.tumblr.timelinefilter.fingerprints.TimelineConstructorFingerprint
import app.revanced.patches.tumblr.timelinefilter.fingerprints.TimelineFilterIntegrationFingerprint

@Patch(description = "Filter what will be shown in the timeline.", requiresIntegrations = true)
object TimelineFilterPatch : BytecodePatch(
    setOf(TimelineConstructorFingerprint, TimelineFilterIntegrationFingerprint, PostsResponseConstructorFingerprint)
) {
    /**
     * Add a filter to hide the given timeline object type.
     * The list of all Timeline object types is found in the TimelineObjectType class,
     * where they are mapped from their api name (returned by tumblr via the HTTP API) to the enum value name.
     *
     * @param typename The enum name of the timeline object type to hide.
     */
    @Suppress("KDocUnresolvedReference")
    internal lateinit var addObjectTypeFilter: (typename: String) -> Unit private set

    override fun execute(context: BytecodeContext) {

        TimelineFilterIntegrationFingerprint.result?.let { integration ->
            val startIndex = integration.scanResult.patternScanResult!!.startIndex

            integration.mutableMethod.apply {
                // Remove "BLOCKED_OBJECT_DUMMY" object type filter
                removeInstructions(startIndex, 5)

                addObjectTypeFilter = { typename ->
                    // It's too much of a pain to find the register numbers manually, so this will just have to be
                    // updated if the Timeline Filter integration changes
                    // The java equivalent of this is
                    //   if ("BLOCKED_OBJECT_DUMMY".equals(elementType)) iterator.remove();
                    addInstructionsWithLabels(
                        startIndex, """
                            const-string v1, "$typename"
                            invoke-virtual { v1, v0 }, Ljava/lang/String;->equals(Ljava/lang/Object;)Z
                            move-result v1
                            if-eqz v1, :dont_remove
                            invoke-interface { v2 }, Ljava/util/Iterator;->remove()V
                            :dont_remove
                            nop
                        """
                    )
                }
            }
        } ?: throw TimelineFilterIntegrationFingerprint.exception

        TimelineConstructorFingerprint.result?.mutableMethod?.addInstructions(
            0, """
                    invoke-static {p1}, Lapp/revanced/tumblr/patches/TimelineFilterPatch;->filterTimeline(Ljava/util/List;)V
                """
        ) ?: throw TimelineConstructorFingerprint.exception
        PostsResponseConstructorFingerprint.result?.mutableMethod?.addInstructions(
            0, """
                    invoke-static {p2}, Lapp/revanced/tumblr/patches/TimelineFilterPatch;->filterTimeline(Ljava/util/List;)V
                """
        ) ?: throw PostsResponseConstructorFingerprint.exception
    }
}
