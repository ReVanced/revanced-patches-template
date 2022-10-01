package app.revanced.patches.youtube.ad.general.fingerprints

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.annotation.MatchingMethod
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patches.youtube.ad.general.annotations.GeneralAdsRemovalPatchCompatibility
import app.revanced.patches.youtube.ad.general.patch.GeneralAdsRemovalPatch
import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.iface.instruction.WideLiteralInstruction

@Name("promoted-video-item-land-fingerprint")
@MatchingMethod("Ljvl;", "k")
@GeneralAdsRemovalPatchCompatibility
@Version("0.0.1")
object PromotedVideoItemLandFingerprint : MethodFingerprint(
    "Z", AccessFlags.PRIVATE or AccessFlags.FINAL, listOf("L"),
    customFingerprint = { methodDef ->
        methodDef.implementation?.instructions?.any { instruction ->
            (instruction as? WideLiteralInstruction)?.wideLiteral == GeneralAdsRemovalPatch.resourceIds[1]
        } == true
    }
)