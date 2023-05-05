package app.revanced.patches.youtube.layout.theme.bytecode.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patches.shared.mapping.misc.patch.indexOfFirstConstantInstruction
import app.revanced.patches.youtube.layout.theme.resource.ThemeResourcePatch
import org.jf.dexlib2.AccessFlags

object CreateDarkThemeSeekbarFingerprint : MethodFingerprint(
    access = AccessFlags.PUBLIC or AccessFlags.CONSTRUCTOR,
    customFingerprint = { method ->
        method.indexOfFirstConstantInstruction(ThemeResourcePatch.inlineTimeBarColorizedBarPlayedColorDarkId) != -1
    }
)