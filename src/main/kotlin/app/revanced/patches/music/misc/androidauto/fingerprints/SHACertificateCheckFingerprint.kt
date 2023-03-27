package app.revanced.patches.music.misc.androidauto.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import org.jf.dexlib2.AccessFlags


object SHACertificateCheckFingerprint: MethodFingerprint("Z", AccessFlags.PUBLIC or AccessFlags.FINAL,
    strings = listOf(
        "AllowlistManager.java",
        "com/google/android/apps/youtube/music/mediabrowser/AllowlistManager",
        "isPartnerSHA",
        "No match"
    )
)
