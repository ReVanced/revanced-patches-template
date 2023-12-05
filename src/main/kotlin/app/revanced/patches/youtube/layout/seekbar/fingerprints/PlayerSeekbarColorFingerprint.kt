package app.revanced.patches.youtube.layout.seekbar.fingerprints

import app.revanced.util.containsWideLiteralInstructionValue
import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.MethodFingerprint
import app.revanced.patches.youtube.layout.seekbar.SeekbarColorResourcePatch
import com.android.tools.smali.dexlib2.AccessFlags

internal object PlayerSeekbarColorFingerprint : MethodFingerprint(
    accessFlags = AccessFlags.PUBLIC or AccessFlags.CONSTRUCTOR,
    customFingerprint = { method, _ ->
        method.containsWideLiteralInstructionValue(SeekbarColorResourcePatch.inlineTimeBarColorizedBarPlayedColorDarkId)
                && method.containsWideLiteralInstructionValue(SeekbarColorResourcePatch.inlineTimeBarPlayedNotHighlightedColorId)
    }
)