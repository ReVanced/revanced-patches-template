package app.revanced.patches.twitter.ad.timeline.fingerprints

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.fingerprint.method.annotation.MatchingMethod
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patches.twitter.ad.timeline.annotations.TimelineAdsCompatibility
import org.jf.dexlib2.Opcode

@Name("timeline-rtb-image-ad-parser-fingerprint")
@MatchingMethod("LJsonTimelineRtbImageAd\$\$JsonObjectMapper;", "parseField")
@TimelineAdsCompatibility
@Version("0.0.1")
object TimelineRtbImageAdParserFingerprint : MethodFingerprint(
    null, null, null, listOf(
        Opcode.IPUT_OBJECT
    ), listOf("creativeId", "promotedMetadata", "vanityUrl"),
    { methodDef -> methodDef.name == "parseField" }
)