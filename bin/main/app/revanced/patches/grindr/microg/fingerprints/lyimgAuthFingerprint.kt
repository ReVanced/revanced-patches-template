package app.revanced.patches.grindr.microg.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import org.jf.dexlib2.AccessFlags

object lyimgAuthFingerprint : MethodFingerprint(
    returnType = "V",
    strings = listOf("The bundle identifiers that you specified in your license file do not match the app\'s bundle identifier. Please update and redownload your license from your customer dashboard at https://www.photoeditorsdk.com/login.")
)