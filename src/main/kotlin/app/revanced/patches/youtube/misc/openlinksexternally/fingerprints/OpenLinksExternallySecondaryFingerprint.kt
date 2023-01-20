package app.revanced.patches.youtube.misc.openlinksexternally.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.Opcode

object OpenLinksExternallySecondaryFingerprint : MethodFingerprint(
    returnType = "L",
    access = AccessFlags.PUBLIC or AccessFlags.FINAL,
    opcodes = listOf(
        Opcode.IPUT_OBJECT,
        Opcode.NEW_INSTANCE,
        Opcode.CONST_STRING
    ),
    strings = listOf("android.support.customtabs.action.CustomTabsService")
)