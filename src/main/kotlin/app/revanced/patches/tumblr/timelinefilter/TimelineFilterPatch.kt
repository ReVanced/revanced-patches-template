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
                // Remove "BLOCKED_OBJECT_DUMMY" object type filter
                removeInstructions(filterInsertIndex, 5)

                addObjectTypeFilter = { typeName ->
                    // The java equivalent of this is
                    //   if ("BLOCKED_OBJECT_DUMMY".equals(elementType)) iterator.remove();
                    addInstructionsWithLabels(
                        filterInsertIndex, """
                            const-string v1, "$typeName"
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

        arrayOf(TimelineConstructorFingerprint, PostsResponseConstructorFingerprint).forEach {
            it.result?.mutableMethod?.addInstructions(
                0,
                "invoke-static {p1}, Lapp/revanced/tumblr/patches/TimelineFilterPatch;->" +
                        "filterTimeline(Ljava/util/List;)V"
            ) ?: throw it.exception
        }
    }
}
