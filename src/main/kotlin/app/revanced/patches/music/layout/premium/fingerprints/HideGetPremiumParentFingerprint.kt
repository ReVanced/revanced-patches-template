package app.revanced.patches.music.layout.premium.fingerprints

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patcher.fingerprint.method.annotation.MatchingMethod
import app.revanced.patches.music.layout.premium.annotations.HideGetPremiumCompatibility
import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.Opcode

@Name("hide-get-premium-parent-fingerprint")
@MatchingMethod(
    "Lktn;", "i"
)
@HideGetPremiumCompatibility
@Version("0.0.1")
object HideGetPremiumParentFingerprint : MethodFingerprint(
    "L",
    AccessFlags.PUBLIC or AccessFlags.FINAL,
    listOf(),
    null,
    listOf("avatar_menu_activate_switch_account"),
    null
)