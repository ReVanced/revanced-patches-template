package app.revanced.patches.youtube.misc.fix.playback.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import org.jf.dexlib2.AccessFlags

object SubtitleWindowSettingsConstructorFingerprint : MethodFingerprint(
    accessFlags = AccessFlags.PUBLIC or AccessFlags.CONSTRUCTOR,
    parameters = listOf("I", "I", "I", "Z", "Z"),
    customFingerprint = { methodDef, _ ->
        methodDef.definingClass == "Lcom/google/android/libraries/youtube/player/subtitles/model/SubtitleWindowSettings;"
                && methodDef.name == "<init>"
    }
)