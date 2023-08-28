package app.revanced.patches.reddit.customclients.infinityforreddit.api.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import com.android.tools.smali.dexlib2.iface.ClassDef
import com.android.tools.smali.dexlib2.iface.Method

abstract class AbstractClientIdFingerprint(
    returnType: String,
    customFingerprint: ((methodDef: Method, classDef: ClassDef) -> Boolean)? = null
) : MethodFingerprint(
    returnType = returnType,
    strings = listOf("NOe2iKrPPzwscA"),
    customFingerprint = customFingerprint
)