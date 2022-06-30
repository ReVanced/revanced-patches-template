package app.revanced.patches.youtube.layout.autorepeat.fingerprints

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patcher.fingerprint.method.annotation.FuzzyPatternScanMethod
import app.revanced.patcher.fingerprint.method.annotation.MatchingMethod
import app.revanced.patches.youtube.layout.autorepeat.annotations.AutoRepeatCompatibility
import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.Opcode

@Name("auto-repeat-fingerprint")
@MatchingMethod(
    "Ljvy;", "<init>"
)
@FuzzyPatternScanMethod(2) // FIXME: Test this threshold and find the best value.
@AutoRepeatCompatibility
@Version("0.0.1")
object AutoRepeatFingerprint : MethodFingerprint(
    null,
    null,
    null,
    null,
    null,
    null
)