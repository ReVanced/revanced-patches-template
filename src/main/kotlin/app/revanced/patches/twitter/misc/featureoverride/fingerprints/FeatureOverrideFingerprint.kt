package app.revanced.patches.twitter.misc.featureoverride.fingerprints

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patches.music.layout.minimizedplayback.annotations.MinimizedPlaybackCompatibility
import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.Opcode

object FeatureOverrideFingerprint : MethodFingerprint(
    "Z",
    AccessFlags.PUBLIC or AccessFlags.FINAL,
    listOf("L", "Z"),
    listOf(
        Opcode.CONST_4,
        Opcode.INVOKE_VIRTUAL,
        Opcode.MOVE_RESULT_OBJECT,
        Opcode.IF_EQZ,
        Opcode.INVOKE_STATIC,
        Opcode.MOVE_RESULT,
    )
)
