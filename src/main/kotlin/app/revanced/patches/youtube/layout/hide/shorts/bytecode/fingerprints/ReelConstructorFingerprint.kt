package app.revanced.patches.youtube.layout.hide.shorts.bytecode.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.util.patch.LiteralValueFingerprint
import app.revanced.patches.youtube.layout.hide.shorts.resource.patch.HideShortsComponentsResourcePatch
import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.Opcode

object ReelConstructorFingerprint : LiteralValueFingerprint(
    accessFlags = AccessFlags.PUBLIC or AccessFlags.CONSTRUCTOR,
    opcodes = listOf(Opcode.INVOKE_VIRTUAL),
    literal = HideShortsComponentsResourcePatch.reelMultipleItemShelfId
)