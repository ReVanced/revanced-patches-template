package app.revanced.patches.youtube.layout.sponsorblock.bytecode.fingerprints

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.fingerprint.method.annotation.DirectPatternScanMethod
import app.revanced.patcher.fingerprint.method.annotation.MatchingMethod
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patches.youtube.layout.sponsorblock.annotations.SponsorBlockCompatibility
import org.jf.dexlib2.Opcode

@Name("watch-while-active-fingerprint")
@MatchingMethod(
    "LWatchWhileActive;", "onCreate"
)
@DirectPatternScanMethod
@SponsorBlockCompatibility
@Version("0.0.1")
object WatchWhileActiveFingerprint : MethodFingerprint(
    null, null, null, listOf(Opcode.INVOKE_DIRECT_RANGE), null, { methodDef ->
        methodDef.definingClass.endsWith("WatchWhileActive;") && methodDef.name == "onCreate"
    }
)