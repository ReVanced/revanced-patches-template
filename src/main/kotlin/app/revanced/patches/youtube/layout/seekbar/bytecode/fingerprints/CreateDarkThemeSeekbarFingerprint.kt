package app.revanced.patches.youtube.layout.seekbar.bytecode.fingerprints

import app.revanced.extensions.containsConstantInstructionValue
import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patches.youtube.layout.seekbar.resource.SeekbarColorResourcePatch
import org.jf.dexlib2.AccessFlags

object CreateDarkThemeSeekbarFingerprint : MethodFingerprint(
    accessFlags = AccessFlags.PUBLIC or AccessFlags.CONSTRUCTOR,
    customFingerprint = { method, _ ->
        method.containsConstantInstructionValue(SeekbarColorResourcePatch.inlineTimeBarColorizedBarPlayedColorDarkId)
                && method.containsConstantInstructionValue(SeekbarColorResourcePatch.inlineTimeBarPlayedNotHighlightedColorId)
    }
)