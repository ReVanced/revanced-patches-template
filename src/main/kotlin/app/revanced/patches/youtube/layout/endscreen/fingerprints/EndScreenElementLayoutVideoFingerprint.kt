package app.revanced.patches.youtube.layout.endscreen.fingerprints

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.fingerprint.method.annotation.MatchingMethod
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patches.youtube.ad.general.annotations.GeneralAdsRemovalPatchCompatibility
import app.revanced.patches.youtube.ad.general.patch.GeneralAdsRemovalPatch
import app.revanced.patches.youtube.layout.endscreen.patch.HideEndScreenPatch
import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.iface.instruction.WideLiteralInstruction

@Name("end-screen-element-layout-video-fingerprint")
@MatchingMethod("Laagn;", "c")
@GeneralAdsRemovalPatchCompatibility
@Version("0.0.1")
object EndScreenElementLayoutVideoFingerprint : MethodFingerprint(
    "L",
    AccessFlags.PUBLIC.value,
    customFingerprint = { methodDef ->
        methodDef.implementation?.instructions?.any { instruction ->
            (instruction as? WideLiteralInstruction)?.wideLiteral == HideEndScreenPatch.resourceIds[2]
        } == true
    }
)