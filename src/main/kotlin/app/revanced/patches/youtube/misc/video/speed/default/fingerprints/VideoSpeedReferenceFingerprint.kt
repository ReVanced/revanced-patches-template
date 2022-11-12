package app.revanced.patches.youtube.misc.video.speed.default.fingerprints

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.annotation.YouTubeCompatibility
import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.Opcode

@Name("video-speed-reference-fingerprint")
@YouTubeCompatibility
@Version("0.0.1")
object VideoSpeedReferenceFingerprint : MethodFingerprint(
    "V", AccessFlags.PUBLIC or AccessFlags.FINAL, listOf("L"), listOf(
        Opcode.IPUT_OBJECT, Opcode.RETURN_VOID
    )
)
