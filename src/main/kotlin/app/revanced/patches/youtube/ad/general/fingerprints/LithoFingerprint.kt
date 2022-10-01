package app.revanced.patches.youtube.ad.general.fingerprints

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.annotation.MatchingMethod
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patches.youtube.ad.general.annotations.GeneralAdsRemovalPatchCompatibility
import org.jf.dexlib2.AccessFlags

@Name("litho-fingerprint")
@MatchingMethod("Lnvb;", "c")
@GeneralAdsRemovalPatchCompatibility
@Version("0.0.1")
object LithoFingerprint : MethodFingerprint(
    "L", AccessFlags.PUBLIC or AccessFlags.FINAL, listOf("L", "L", "L", "L", "L", "I", "Z"),
    strings = listOf("Element missing type extension")
)