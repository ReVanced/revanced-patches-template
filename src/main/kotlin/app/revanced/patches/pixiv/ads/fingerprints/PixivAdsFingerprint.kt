package app.revanced.patches.pixiv.ads.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import org.jf.dexlib2.AccessFlags

object PixivAdsFingerprint : MethodFingerprint(
    "V",
    AccessFlags.PUBLIC or AccessFlags.CONSTRUCTOR,
    listOf("L"),
    // FIXME: Possibly remove this strcmp in the future? This makes the fingerprint really easy to break.
    strings = listOf("pixivAccountManager"),
    customFingerprint = { _, cls ->
        cls.virtualMethods.count() == 1 && cls.virtualMethods.first().let {
            it.parameterTypes.size == 0 && it.returnType == "Z"
        }
    }
)