package app.revanced.patches.youtube.misc.settings.bytecode.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patches.youtube.misc.settings.resource.patch.YouTubeSettingsResourcePatch
import app.revanced.util.patch.LiteralValueFingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

object SetThemeFingerprint : LiteralValueFingerprint(
    accessFlags = AccessFlags.PUBLIC or AccessFlags.FINAL,
    returnType = "L",
    parameters = listOf(),
    opcodes = listOf(Opcode.RETURN_OBJECT),
    literal = YouTubeSettingsResourcePatch.appearanceStringId
)