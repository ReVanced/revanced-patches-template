package app.revanced.patches.youtube.layout.endscreen.fingerprints

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.annotation.MatchingMethod
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patches.youtube.ad.general.annotations.GeneralAdsRemovalPatchCompatibility
import app.revanced.patches.youtube.ad.general.patch.GeneralAdsRemovalPatch
import app.revanced.patches.youtube.layout.endscreen.patch.HideEndScreenPatch
import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.iface.instruction.WideLiteralInstruction

@Name("end-screen-element-layout-icon-fingerprint")
@MatchingMethod("Laagv;", "c")
@GeneralAdsRemovalPatchCompatibility
@Version("0.0.1")
object EndScreenElementLayoutIconFingerprint : MethodFingerprint(
    "L",
    AccessFlags.PUBLIC or AccessFlags.FINAL,
    customFingerprint = { methodDef ->
        methodDef.implementation?.instructions?.any { instruction ->
            (instruction as? WideLiteralInstruction)?.wideLiteral == HideEndScreenPatch.resourceIds[1]
        } == true
    }
)