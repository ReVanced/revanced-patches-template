package app.revanced.patches.youtube.misc.minimizedplayback.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.MethodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags

/**
 * Class fingerprint for [MinimizedPlaybackSettingsFingerprint]
 */
internal object MinimizedPlaybackSettingsParentFingerprint : MethodFingerprint(
    accessFlags = AccessFlags.PRIVATE or AccessFlags.FINAL,
    returnType = "I",
    parameters = listOf(),
    strings = listOf("BiometricManager", "Failure in canAuthenticate(). FingerprintManager was null.")
)
