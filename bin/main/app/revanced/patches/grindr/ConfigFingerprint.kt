package app.revanced.patches.grindr

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import org.jf.dexlib2.Opcode

object ConfigFingerprint : MethodFingerprint(
    strings = listOf("com.grindrapp.android"),
)