package app.revanced.patches.youtube.layout.hidealbumcards.bytecode.fingerprints

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patches.youtube.layout.hidealbumcards.annotations.AlbumCardsCompatibility
import app.revanced.patches.youtube.layout.hidealbumcards.resource.patch.AlbumCardsResourcePatch
import org.jf.dexlib2.iface.instruction.WideLiteralInstruction

@Name("album-cards-view-fingerprint")
@AlbumCardsCompatibility
@Version("0.0.1")
object AlbumCardsFingerprint : MethodFingerprint(
    customFingerprint = { methodDef ->
        methodDef.implementation?.instructions?.any { instruction ->
            (instruction as? WideLiteralInstruction)?.wideLiteral == AlbumCardsResourcePatch.albumCardId
        } == true
    }
)