package app.revanced.patches.youtube.layout.sponsorblock.bytecode.fingerprints

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.annotation.DirectPatternScanMethod
import app.revanced.patcher.fingerprint.method.annotation.MatchingMethod
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patches.youtube.layout.sponsorblock.annotations.SponsorBlockCompatibility
import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.Opcode

@Name("video-id-fingerprint")
@MatchingMethod(
    "Lcom/google/android/apps/youtube/app/common/player/PlaybackLifecycleMonitor;", "l"
)
@DirectPatternScanMethod
@SponsorBlockCompatibility
@Version("0.0.1")
object VideoIdFingerprint : MethodFingerprint(
    "V",
    AccessFlags.DECLARED_SYNCHRONIZED or AccessFlags.FINAL or AccessFlags.PUBLIC,
    listOf("L"),
    listOf(Opcode.INVOKE_INTERFACE),
    customFingerprint = {
        it.definingClass.endsWith("PlaybackLifecycleMonitor;")
    }
)