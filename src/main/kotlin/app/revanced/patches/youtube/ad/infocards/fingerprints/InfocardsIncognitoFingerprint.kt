package app.revanced.patches.youtube.ad.infocards.fingerprints

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.annotation.FuzzyPatternScanMethod
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patches.youtube.ad.infocards.annotations.HideInfocardsCompatibility
import org.jf.dexlib2.AccessFlags

@Name("infocards-incognito-fingerprint")
@FuzzyPatternScanMethod(2)
@HideInfocardsCompatibility
@Version("0.0.1")
object InfocardsIncognitoFingerprint : MethodFingerprint(
    "Ljava/lang/Boolean;",
    AccessFlags.PUBLIC or AccessFlags.FINAL,
    strings = listOf("vibrator")
)