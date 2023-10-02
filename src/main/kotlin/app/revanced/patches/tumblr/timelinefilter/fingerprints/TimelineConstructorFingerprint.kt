package app.revanced.patches.tumblr.timelinefilter.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint

// This is the constructor of the Timeline class.
// It receives the List<TimelineObject> as an argument with a @Json annotation, so this should be the first time
// that the List<TimelineObject> is exposed in non-library code.
object TimelineConstructorFingerprint : MethodFingerprint(
    customFingerprint = { methodDef, _ ->
        methodDef.definingClass.endsWith("/Timeline;") && methodDef.parameters[0].type == "Ljava/util/List;"
    }, strings = listOf("timelineObjectsList")
)