package app.revanced.patches.youtube.layout.infocards.fingerprints

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patches.youtube.layout.infocards.annotations.HideInfoCardsCompatibility
import app.revanced.patches.youtube.layout.infocards.patch.HideInfoCardsPatch
import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.iface.instruction.WideLiteralInstruction

@Name("info-cards-drawer-header-fingerprint")
@HideInfoCardsCompatibility
@Version("0.0.1")
object InfoCardsDrawerHeaderFingerprint : MethodFingerprint(
    "V",
    AccessFlags.PUBLIC or AccessFlags.FINAL,
    listOf("L", "L", "L"),
    customFingerprint = { methodDef ->
        methodDef.implementation?.instructions?.any { instruction ->
            (instruction as? WideLiteralInstruction)?.wideLiteral == HideInfoCardsPatch.drawerResourceId
        } == true
    }
)