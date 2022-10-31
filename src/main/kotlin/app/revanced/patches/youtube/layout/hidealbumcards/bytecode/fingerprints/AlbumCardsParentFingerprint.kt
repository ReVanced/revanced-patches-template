package app.revanced.patches.youtube.layout.hidealbumcards.bytecode.fingerprints

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patches.youtube.layout.hidealbumcards.annotations.AlbumCardsCompatibility
import org.jf.dexlib2.Opcode

@Name("album-cards-view-fingerprint")
@AlbumCardsCompatibility
@Version("0.0.1")
object AlbumCardsParentFingerprint : MethodFingerprint(
    opcodes = listOf(
        Opcode.CONST,
        Opcode.CONST_4,
        Opcode.INVOKE_VIRTUAL,
        Opcode.MOVE_RESULT_OBJECT,
        Opcode.CHECK_CAST,
    ),
)