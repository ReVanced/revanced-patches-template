package app.revanced.patches.music.ad.video.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.MethodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

internal object ShowMusicVideoAdsFingerprint : MethodFingerprint(
    "V", AccessFlags.PUBLIC or AccessFlags.FINAL, listOf("Z"), listOf(
        Opcode.IPUT_BOOLEAN,
        Opcode.INVOKE_VIRTUAL,
        Opcode.RETURN_VOID
    )
)
