package app.revanced.patches.youtube.layout.infocards.fingerprints

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.annotation.MatchingMethod
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patches.youtube.layout.infocards.annotations.HideInfoCardsCompatibility
import org.jf.dexlib2.AccessFlags

@Name("info-cards-parent-fingerprint")
@MatchingMethod("Liff;", "lE")
@HideInfoCardsCompatibility
@Version("0.0.1")
object InfoCardsParentFingerprint : MethodFingerprint(
    "Ljava/lang/String;",
    AccessFlags.PUBLIC or AccessFlags.FINAL,
    strings = listOf("player_overlay_info_card_teaser"),
)