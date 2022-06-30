package app.revanced.patches.youtube.layout.autorepeat.fingerprints

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patcher.fingerprint.method.annotation.FuzzyPatternScanMethod
import app.revanced.patcher.fingerprint.method.annotation.MatchingMethod
import app.revanced.patches.youtube.layout.autorepeat.annotations.AutoRepeatCompatibility
import org.jf.dexlib2.AccessFlags

@Name("auto-repeat-fingerprint")
@MatchingMethod(
    "Ljvy;", "<init>"
)
@FuzzyPatternScanMethod(2) // FIXME: Test this threshold and find the best value.
@AutoRepeatCompatibility
@Version("0.0.1")
//ToDo: Find method:
/*
public final void ae() {
        aq(aabj.ENDED);
    }
 */
//Change method to:
/*
public final void ae() {
    aq(aabj.ENDED);
    app.revanced.integrations.sponsorblock.player.VideoInformation.videoEnded();
    if (app.revanced.integrations.patches.AutoRepeatPatch.shouldAutoRepeat()) {
        //Method E plays the same video again. Is found by the Parent Fingerprint.
        E();
    }
}
*/
object AutoRepeatFingerprint : MethodFingerprint(
    "V",
    AccessFlags.PUBLIC or AccessFlags.FINAL,
    null,
    null,
    null,
    null
)