package app.revanced.patches.youtube.layout.sponsorblock.fingerprints

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.fingerprint.method.annotation.DirectPatternScanMethod
import app.revanced.patcher.fingerprint.method.annotation.MatchingMethod
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patches.youtube.layout.sponsorblock.annotations.SponsorBlockCompatibility
import org.jf.dexlib2.Opcode

@Name("player-controller-set-time-reference-fingerprint")
@MatchingMethod(
    "Lxqm;", "<init>"
)
@DirectPatternScanMethod
@SponsorBlockCompatibility
@Version("0.0.1")
object PlayerControllerSetTimeReferenceFingerprint : MethodFingerprint(
    null, null, null,
    listOf(Opcode.INVOKE_DIRECT_RANGE,),
    listOf("Media progress reported outside media playback: ")
)