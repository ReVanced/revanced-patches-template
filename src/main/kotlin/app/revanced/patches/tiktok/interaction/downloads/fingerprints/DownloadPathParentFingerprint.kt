package app.revanced.patches.tiktok.interaction.downloads.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.MethodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags

internal object DownloadPathParentFingerprint : MethodFingerprint(
    "V",
    AccessFlags.PUBLIC or AccessFlags.FINAL,
    strings = listOf(
        "code",
        "reason",
        "params insufficient"
    )
)