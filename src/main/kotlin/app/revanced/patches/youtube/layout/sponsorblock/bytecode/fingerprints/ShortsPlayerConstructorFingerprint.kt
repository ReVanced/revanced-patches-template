package app.revanced.patches.youtube.layout.sponsorblock.bytecode.fingerprints

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.annotation.MatchingMethod
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patches.youtube.layout.sponsorblock.annotations.SponsorBlockCompatibility
import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.Opcode

@Name("shortsplayer-constructor-fingerprint")
@MatchingMethod("Lhgp;", "<init>")
@SponsorBlockCompatibility
@Version("0.0.1")
object ShortsPlayerConstructorFingerprint : MethodFingerprint(
    "V", AccessFlags.PUBLIC or AccessFlags.CONSTRUCTOR, listOf("L", "L", "L", "L", "L", "L", "L", "L", "L", "L", "L", "L", "L", "L", "L", "L", "L", "L", "L", "L", "L", "L", "L", "[B", "[B", "[B", "[B", "[B"), listOf(
        Opcode.MOVE_OBJECT_FROM16,
        Opcode.MOVE_OBJECT_FROM16,
        Opcode.MOVE_OBJECT_FROM16,
        Opcode.INVOKE_DIRECT_RANGE,
        Opcode.NEW_INSTANCE,
        Opcode.INVOKE_DIRECT,
        Opcode.IPUT_OBJECT,
        Opcode.NEW_INSTANCE,
        Opcode.INVOKE_DIRECT,
        Opcode.IPUT_OBJECT,
        Opcode.NEW_INSTANCE,
        Opcode.INVOKE_DIRECT,
        Opcode.IPUT_OBJECT,
        Opcode.NEW_INSTANCE,
        Opcode.INVOKE_DIRECT,
        Opcode.IPUT_OBJECT,
        Opcode.CONST_4,
        Opcode.IPUT_BOOLEAN,
        Opcode.MOVE_OBJECT_FROM16,
        Opcode.IPUT_BOOLEAN,
        Opcode.MOVE_OBJECT_FROM16,
        Opcode.IPUT_BOOLEAN,
        Opcode.MOVE_OBJECT_FROM16,
        Opcode.IPUT_BOOLEAN,
        Opcode.MOVE_OBJECT_FROM16,
        Opcode.IPUT_BOOLEAN,
    )
)